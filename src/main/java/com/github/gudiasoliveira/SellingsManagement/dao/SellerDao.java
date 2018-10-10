package com.github.gudiasoliveira.SellingsManagement.dao;

import org.springframework.data.repository.CrudRepository;

import com.github.gudiasoliveira.SellingsManagement.models.Seller;

public interface SellerDao extends CrudRepository<Seller, Long> {
}
