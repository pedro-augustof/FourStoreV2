package br.com.fourcamp.fourstorev2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.fourcamp.fourstorev2.enums.PaymentMethodEnum;
import br.com.fourcamp.fourstorev2.exception.ProductNotFoundException;
import br.com.fourcamp.fourstorev2.exception.StockInsufficientException;
import br.com.fourcamp.fourstorev2.model.*;
import br.com.fourcamp.fourstorev2.service.StockService;
import br.com.fourcamp.fourstorev2.service.TransactionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/transaction")
public class TransactionController {

	private TransactionService transactionService;
	private StockService stockService;

	public TransactionController() {
		this.transactionService = new TransactionService();
		this.stockService = new StockService();
	}

	@PostMapping("/purchase")
	public String purchase(@RequestBody Purchase purchase)
			throws StockInsufficientException, ProductNotFoundException {
		Costumer costumer = purchase.getCostumer();

		try{
			if (purchase.getStock() == null || costumer.getPaymentMethod() == null || costumer.getPaymentData() == null){
				return "Compra inválida!";
			}
		} catch (NullPointerException e){
			return "Compra inválida!";
		}

		boolean valid = false;

		switch (PaymentMethodEnum.get(costumer.getPaymentMethod()).getPaymentMethodId()){
			case 3:
				if (isValidCardNumber(costumer.getPaymentData())){
					valid = true;
				};
				break;
			case 4:
				if (isValidCardNumber(costumer.getPaymentData())){
					valid = true;
				};
				break;
			case 5:
				if (isCellPhone(costumer.getPaymentData()) || isCnpj(costumer.getPaymentData()) || isCpf(costumer.getPaymentData()) || isEmail(costumer.getPaymentData()) || isRandomKey(costumer.getPaymentData())){
					valid = true;
				}
				break;
			default:
				valid = true;
		}

		if (!valid){
			return "Pagamento inválido!";
		}

		Transaction transaction = new Transaction(costumer);

		for (Stock stock : purchase.getStock()) {
			Product product = stock.getProduct();
			Integer quantity = stock.getQuantity();
			transaction.addProducts(product, quantity);
			if (stockService.validateIndividualPurchase(product.getSku(), quantity) == null){
				return "Quantidade insuficiente!";
			}
		}

		String txt = transactionService.createTransaction(transaction);
		transaction.setTotalPrice();
		String totalPrice = String.format("R$%.2f", transaction.getTotalPrice());
		txt += "\nCompra realizada com sucesso!\nValor total: " + totalPrice;
		return txt;
	}

	@GetMapping("/listall")
	public String listAll() {
		Double totalProfit = 0.0;
		String totalList = "";
		List<Transaction> list = transactionService.listAll();
		for (Transaction transaction : list) {
			totalList += transaction.toString() + "\n\n";
			totalProfit += transaction.getTotalPrice();
		}
		totalList += String.format("\nSoma do valor das vendas: R$ %.2f", totalProfit);
		return totalList;
	}

	public boolean isValidCardNumber(String number) {
		if (12 > number.length() || number.length() > 19) {
			return false;
		}

		if (!number.matches("[0-9]+")) {
			return false;
		}

		return isValidLuhn(number);
	}

	private boolean isValidLuhn(String number) {
		Integer invertedNumber[] = new Integer[number.length() - 1];
		Integer lastNumber;
		Integer tempNumber;
		Integer sum = 0;

		for (int i = number.length() - 1; i > -1; i--) {
			if (i == number.length() - 1) {
				lastNumber = Integer.parseInt(number.substring(i));
				sum += lastNumber;
			} else {
				invertedNumber[i] = Integer.parseInt(number.substring(i, i + 1));
			}
		}

		for (int i = 0; i < invertedNumber.length; i++) {
			if (i % 2 != 0) {
				sum += invertedNumber[i];
			} else {
				tempNumber = invertedNumber[i] * 2;

				if (tempNumber > 9) {
					tempNumber -= 9;
				}

				sum += tempNumber;
			}
		}

		return (sum % 10 == 0);
	}

	public boolean isCpf(String cpf) {
		Integer multiply = 10;
		Integer totalSum = 0;
		Integer firstDigit;
		Integer lastDigit;

		if (cpf.length() != 11 || cpf == null || cpf.equals("") || cpf.equals("00000000000")
				|| cpf.equals("11111111111") || cpf.equals("22222222222") || cpf.equals("33333333333")
				|| cpf.equals("44444444444") || cpf.equals("55555555555") || cpf.equals("66666666666")
				|| cpf.equals("77777777777") || cpf.equals("88888888888") || cpf.equals("99999999999")) {
			return false;
		}

		for (int i = 0; i < cpf.length() - 2; i++) {
			totalSum += Integer.parseInt(cpf.substring(i, i + 1)) * multiply;
			multiply--;
		}

		firstDigit = 11 - (totalSum % 11);
		multiply = 11;
		totalSum = 0;

		for (int i = 0; i < cpf.length() - 2; i++) {
			totalSum += Integer.parseInt(cpf.substring(i, i + 1)) * multiply;
			multiply--;
		}

		totalSum += firstDigit * 2;
		lastDigit = 11 - (totalSum % 11);

		return (firstDigit == Integer.parseInt(cpf.substring(9, 10))
				&& lastDigit == Integer.parseInt(cpf.substring(10)));
	}

	public boolean isCnpj(String cnpj) {
		if (cnpj.equals("00000000000000") || cnpj.equals("11111111111111") || cnpj.equals("22222222222222")
				|| cnpj.equals("33333333333333") || cnpj.equals("44444444444444") || cnpj.equals("55555555555555")
				|| cnpj.equals("66666666666666") || cnpj.equals("77777777777777") || cnpj.equals("88888888888888")
				|| cnpj.equals("99999999999999") || cnpj.length() != 14 || cnpj == null || cnpj.equals("")) {
			return false;
		}

		Integer invertedCnpj[] = new Integer[cnpj.length() - 2];
		Integer multiply = 2;
		Integer position = 0;
		Integer totalSum = 0;
		Integer firstDigit, lastDigit, digit;

		for (int i = invertedCnpj.length - 1; i > -1; i--) {
			if (multiply > 9) {
				multiply = 2;
			}

			invertedCnpj[position] = Integer.parseInt(cnpj.substring(i, i + 1));
			totalSum += invertedCnpj[position++] * multiply++;
		}

		digit = totalSum % 11;

		if (digit == 0 || digit == 1) {
			firstDigit = 0;
		} else {
			firstDigit = 11 - digit;
		}

		multiply = 2;
		totalSum = firstDigit * multiply++;

		for (int i = 0; i < invertedCnpj.length; i++) {
			if (multiply > 9) {
				multiply = 2;
			}

			totalSum += invertedCnpj[i] * multiply++;
		}
		digit = totalSum % 11;

		if (digit == 0 || digit == 1) {
			lastDigit = 0;
		} else {
			lastDigit = 11 - digit;
		}

		return (firstDigit == Integer.parseInt(cnpj.substring(12, 13))
				&& lastDigit == Integer.parseInt(cnpj.substring(13)));
	}

	public boolean isEmail(String email) {
		if (email.equals("") || email == null) {
			return false;
		}

		String regx = "^[a-zA-Z0-9_!#$%&'\\*+/=?{|}~^.-]+@[a-zA-Z0-9.-]+$";
		Pattern pattern = Pattern.compile(regx);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}

	public boolean isCellPhone(String phone) {
		if (phone.equals("") || phone == null) {
			return false;
		}

		String rgx = "^([11-99]{2})([9]{1})([0-9]{8})+$";
		Pattern pattern = Pattern.compile(rgx);
		Matcher matcher = pattern.matcher(phone);

		return matcher.matches();
	}

	public boolean isRandomKey(String key) {
		if (key.length() != 36 || key == null || key.equals("")) {
			return false;
		}

		String regex = "^([a-zA-Z0-9'\\\\-])+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(key);

		return matcher.matches();
	}
}
