package br.com.fourcamp.fourstorev2.data;

import java.util.ArrayList;
import java.util.List;

import br.com.fourcamp.fourstorev2.interfaces.DataInterface;
import br.com.fourcamp.fourstorev2.model.Transaction;

public class TransactionData implements DataInterface {

	public static List<Transaction> list = new ArrayList<Transaction>();

	@Override
	public void save(Object object) {
		list.add((Transaction)object);
	}

	@Override
	public List<Transaction> findAll() {
		return list;
	}

}
