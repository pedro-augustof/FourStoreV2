package br.com.fourcamp.fourstorev2.model;

import java.util.List;

public class Purchase {
    private List<Stock> stock;
    private Costumer costumer;

    public List<Stock> getStock() {
        return stock;
    }

    public void setStock(List<Stock> stock) {
        this.stock = stock;
    }

    public Costumer getCostumer() {
        return costumer;
    }

    public void setCostumer(Costumer costumer) {
        this.costumer = costumer;
    }
}
