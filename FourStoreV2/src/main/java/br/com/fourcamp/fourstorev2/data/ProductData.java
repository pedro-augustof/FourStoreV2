package br.com.fourcamp.fourstorev2.data;

import br.com.fourcamp.fourstorev2.interfaces.DataInterface;
import br.com.fourcamp.fourstorev2.model.Product;
import br.com.fourcamp.fourstorev2.model.Stock;

import java.util.ArrayList;
import java.util.List;


public class ProductData implements DataInterface {

	public static List<Stock> productList = new ArrayList<>();
	
	@Override
	public void save(Object object) {

		Stock stock = (Stock) object;
		productList.add(stock);
		
	}

	public void deleteBySku(String sku) {
		for (Stock stock : productList) {
			String product = stock.getProduct().getSku();
			if (product.equals(sku)) {
				productList.remove(stock);
				return;
			}
		}
	}

	@Override
	public List<Stock> findAll() {
		return productList;
	}

	public Integer getQuantity(String sku) {

		for (Stock stock : productList) {
			if (stock.getProduct().getSku().equals(sku)) {
				return stock.getQuantity();
			}
		}
		return null;

	}

	public Product findBySku(String sku) {
		for (Stock stock : productList) {
			if (stock.getProduct().getSku().equals(sku)) {
				return stock.getProduct();
			}
		}
		return null;
	}

	public void setQuantity(Object object) {
		for (int i = 0; i < productList.size(); i++) {
			Stock p = productList.get(i);
			Stock stock = (Stock) object;
			String sku = stock.getProduct().getSku();
			Integer newQuantity = stock.getQuantity();
			Integer quantity = p.getQuantity();
			if (p.getProduct().getSku().equals(sku)) {
				p.setQuantity(newQuantity);
			}
		}

	}

}
