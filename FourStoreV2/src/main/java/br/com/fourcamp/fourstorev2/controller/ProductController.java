package br.com.fourcamp.fourstorev2.controller;

import java.util.ConcurrentModificationException;
import java.util.List;

import br.com.fourcamp.fourstorev2.exception.InvalidSellValueException;
import br.com.fourcamp.fourstorev2.exception.ProductNotFoundException;
import br.com.fourcamp.fourstorev2.model.Product;
import br.com.fourcamp.fourstorev2.model.Stock;
import br.com.fourcamp.fourstorev2.service.StockService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
	private StockService stock;

	public ProductController() {
		this.stock = new StockService();
	}

	@GetMapping("/validate")
	public Stock validatePrePurchase(@RequestParam int quantity, @RequestParam String sku) throws ProductNotFoundException {
		try{
			stock.verifyIfExists(sku);
		} catch (NullPointerException e){
			return null;
		}
		if (quantity < 1) {
			return null;
		}
		Product product = stock.validateIndividualPurchase(sku, quantity);
		return new Stock(product, quantity);
	}

	@PostMapping("/insert")
	public String insertProduct(@RequestBody Stock stockObj)
			throws InvalidSellValueException, ProductNotFoundException {
		if (stockObj.getProduct().getSku().length() != 16) {
			return "SKU inválida!";
		}
		if (stockObj.getProduct().getDescription() == null || stockObj.getQuantity() <= 0 || stockObj.getProduct().getBuyPrice() <= 0.0 || stockObj.getProduct().getSellPrice() <= 0.0) {
			return "Produto inválido!";
		}
		try{
			stock.verifyIfExists(stockObj.getProduct().getSku());
			return "Produto já está cadastrado no estoque!";
		} catch (NullPointerException e){}

		stockObj.getProduct().parseSku(stockObj.getProduct().getSku());
		return stock.createProductStock(stockObj.getProduct(), stockObj.getQuantity());
	}

	@GetMapping("/find")
	public String findSku(@RequestBody Product product) throws ProductNotFoundException {
		try {
			return stock.verifyIfExists(product.getSku()).toString();
		} catch (NullPointerException e){
			return "Sku não encontrado!";
		}
	}

	@GetMapping("/listall")
	public String listAll() {
		 List<Stock> stocks = stock.listAll();
		String txt = "";
		for (Stock stock : stocks) {
			txt += stock.getProduct().toString() + ". quantidade: " + stock.getQuantity() + "\n";
		}
		return txt;
	}

	@PostMapping("/delete")
	public String delete(@RequestBody Product sku) throws ProductNotFoundException {
		try{
			stock.verifyIfExists(sku.getSku());
		} catch (NullPointerException e){
			return "Produto não está listado no estoque!";
		}
		stock.deleteBySku(sku.getSku());
		return "Produto excluído com sucesso";
	}

	@PostMapping("/update/quantity")
	public String update(@RequestBody Stock stockObj) throws ProductNotFoundException {
		if (stockObj.getQuantity() <= 0) {
			return "Digite uma quantidade maior que 0";
		}

		try {
			Product product = stock.verifyIfExists(stockObj.getProduct().getSku());
		} catch (NullPointerException e){
			return " Produto não encontrado!";
		}

		stock.reStock(stockObj.getProduct().getSku(), stockObj.getQuantity());
		return " Produto atualizado com sucesso ";
	}

	@PostMapping("/update/price")
	public String update(@RequestBody Product product)
			throws ProductNotFoundException, InvalidSellValueException {
		Product validProduct;
		try {
			validProduct = stock.verifyIfExists(product.getSku());
		} catch (NullPointerException e){
			return " Produto não encontrado!";
		}

		try {
			validProduct.setBuyPrice(product.getBuyPrice());
			validProduct.setSellPrice(product.getSellPrice());
			stock.validateProfit(validProduct);
			return "Produto atualizado com sucesso ";
		} catch (InvalidSellValueException e){
			return "Lucro menor que 25%. Operação inválida!";
		}
	}

}
