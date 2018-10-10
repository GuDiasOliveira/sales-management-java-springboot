package com.github.gudiasoliveira.SellingsManagement.dao;

import org.springframework.data.repository.CrudRepository;

import com.github.gudiasoliveira.SellingsManagement.models.Sale;
import com.github.gudiasoliveira.SellingsManagement.models.Seller;

public interface SaleDao extends CrudRepository<Sale, Long> {
	
	Iterable<Sale> findBySeller(Seller seller);
}
