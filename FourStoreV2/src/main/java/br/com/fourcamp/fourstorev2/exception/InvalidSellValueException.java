package br.com.fourcamp.fourstorev2.exception;

public class InvalidSellValueException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidSellValueException() {
        super("Lucro menor que 25%. Operação inválida!");
	
}
}
