package com.github.gudiasoliveira.SellingsManagement.dao;

import org.springframework.data.repository.CrudRepository;

import com.github.gudiasoliveira.SellingsManagement.models.Sale;

public interface SaleDao extends CrudRepository<Sale, Long> {
}
