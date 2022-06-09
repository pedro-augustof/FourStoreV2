package br.com.fourcamp.fourstorev2.model;

import java.util.HashMap;

public class Transaction {
	private static Integer superId = 0;
	private Integer id;
	private HashMap<Product, Integer> products;
	private Costumer constumer;
	private Double totalPrice;
	
	public Transaction(Costumer constumer) {
		superId++;
		this.id = superId;
		this.constumer = constumer;
		this.totalPrice = 0.0;
		products = new HashMap<Product, Integer>();
	}

	public Transaction(){}

	public Costumer getConstumer() {
		return constumer;
	}
	
	public Integer getId() {
		return id;
	}
	
	public HashMap<Product, Integer> getProducts() {
		return products;
	}

	public void setTotalPrice() {
		products.forEach((key, value) -> {
			this.totalPrice += key.getSellPrice() * value;			
		});
		
	}

	public Double getTotalPrice() {		
		return totalPrice;
	}

	public void setProducts(HashMap<Product, Integer> products){
		this.products = products;
	}

	public void addProducts(Product product, Integer quantityProduct) {
		this.products.put(product, quantityProduct);
	}

	@Override
	public String toString() {
		return "Transa��o com id " + id + ": Cliente " + constumer.toString() + "\nValor da venda "
				+ String.format("R$%.2f", getTotalPrice());
	}
	
}
