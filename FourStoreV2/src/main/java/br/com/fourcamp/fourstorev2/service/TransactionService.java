package br.com.fourcamp.fourstorev2.service;

import java.util.List;

import br.com.fourcamp.fourstorev2.data.TransactionData;
import br.com.fourcamp.fourstorev2.exception.ProductNotFoundException;
import br.com.fourcamp.fourstorev2.exception.StockInsufficientException;
import br.com.fourcamp.fourstorev2.model.Transaction;

public class TransactionService {

	private TransactionData transactionData;
	private StockService stockService;

	public TransactionService() {
		this.transactionData = new TransactionData();
		this.stockService = new StockService();
	}

	public String createTransaction(Transaction transaction)
			throws StockInsufficientException, ProductNotFoundException {
		if (stockService.validatePurchase(transaction)) {
			Transaction savedTransaction = setTransaction(transaction);
			return createMessageResponse(savedTransaction.getId(), " criada.");
		}
		
		return "Transa��o n�o criada!";
	}

	public List<Transaction> listAll() {
		List<Transaction> allTransactions = transactionData.findAll();
		return allTransactions;
	}

	private String createMessageResponse(Integer id, String s) {
		return "Transa��o com a ID " + id + s;
	}

	private Transaction setTransaction(Transaction transaction) {
		transactionData.save(transaction);
		return transaction;
	}
}
