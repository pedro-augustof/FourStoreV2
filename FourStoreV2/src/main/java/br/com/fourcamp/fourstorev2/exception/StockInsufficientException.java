package br.com.fourcamp.fourstorev2.exception;

public class StockInsufficientException extends Exception {

	private static final long serialVersionUID = 1L;

	public StockInsufficientException() {
        super("Estoque do produto � insuficiente para realizar a opera��o!");
    }
	
}
