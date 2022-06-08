package br.com.fourcamp.fourstorev2.exception;

public class CustomerNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public CustomerNotFoundException(Long id) {
        super("Nenhum cliente encontrado com o cpf " + id);
    }

}
