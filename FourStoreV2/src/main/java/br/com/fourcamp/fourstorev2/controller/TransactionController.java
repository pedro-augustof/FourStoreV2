package br.com.fourcamp.fourstorev2.controller;

import java.util.List;

import br.com.fourcamp.fourstorev2.exception.ProductNotFoundException;
import br.com.fourcamp.fourstorev2.exception.StockInsufficientException;
import br.com.fourcamp.fourstorev2.model.Costumer;
import br.com.fourcamp.fourstorev2.model.Product;
import br.com.fourcamp.fourstorev2.model.Stock;
import br.com.fourcamp.fourstorev2.model.Transaction;
import br.com.fourcamp.fourstorev2.service.TransactionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/transaction")
public class TransactionController {

	private TransactionService transactionService;

	public TransactionController() {
		this.transactionService = new TransactionService();
	}

	public String purchase(List<Stock> stocks, String name, Integer payment, String paymentData)
			throws StockInsufficientException, ProductNotFoundException {
		Costumer costumer = new Costumer(name, payment, paymentData);
		Transaction transaction = new Transaction(costumer);
		for (Stock stock : stocks) {
			Product product = stock.getProduct();
			Integer quantity = stock.getQuantity();
			transaction.addProducts(product, quantity);
		}

		String txt = transactionService.createTransaction(transaction);
		transaction.setTotalPrice();
		String totalPrice = String.format("R$%.2f", transaction.getTotalPrice());
		txt += "\nCompra realizada com sucesso!\nValor total: " + totalPrice;
		return txt;
	}

	public String purchase(List<Stock> stocks, String name, String cpf, Integer payment, String paymentData)
			throws StockInsufficientException, ProductNotFoundException {
		Costumer costumer = new Costumer(name, cpf, payment, paymentData);
		Transaction transaction = new Transaction(costumer);
		for (Stock stock : stocks) {
			Product product = stock.getProduct();
			Integer quantity = stock.getQuantity();
			transaction.addProducts(product, quantity);
		}
		String txt = transactionService.createTransaction(transaction);
		transaction.setTotalPrice();
		String totalPrice = String.format("R$%.2f", transaction.getTotalPrice());
		txt += "\nCompra realizada com sucesso!\nValor total: " + totalPrice;
		return txt;
	}

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

}
