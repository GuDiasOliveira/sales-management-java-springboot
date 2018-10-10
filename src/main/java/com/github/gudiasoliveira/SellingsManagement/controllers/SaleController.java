package com.github.gudiasoliveira.SellingsManagement.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.gudiasoliveira.SellingsManagement.dao.SaleDao;
import com.github.gudiasoliveira.SellingsManagement.dao.SellerDao;
import com.github.gudiasoliveira.SellingsManagement.models.Sale;

@RestController
@RequestMapping("/sale")
public class SaleController {
	
	@Autowired
	SaleDao saleDao;
	@Autowired
	SellerDao sellerDao;
	
	@PostMapping("/")
	@ResponseBody
	public Sale create(@RequestBody Sale sale) {
		sale = saleDao.save(sale);
		sale.setSeller(sellerDao.findById(sale.getSeller().getId()).get());
		return sale;
	}
	
	@GetMapping("/")
	public List<Sale> getAll() {
		return toList(saleDao.findAll());
	}
	
	@GetMapping("/{id}")
	public Sale getById(@PathVariable long id) {
		try {
			return saleDao.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new SaleNotFoundException();
		}
	}
	
	@PutMapping("/{id}")
	public Sale update(@PathVariable long id, @Valid @RequestBody Sale sale) {
		sale.setId(id);
		return saleDao.save(sale);
	}
	
	@DeleteMapping("/{id}")
	public Map<String, Object> delete(@PathVariable long id) {
		try {
			saleDao.deleteById(id);
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			return response;
		} catch (EmptyResultDataAccessException e) {
			throw new SaleNotFoundException();
		}
	}
	
	private static <T> List<T> toList(Iterable<T> it) {
		List<T> list = new ArrayList<>();
		for (T el : it)
			list.add(el);
		return list;
	}
	
	@SuppressWarnings("serial")
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	static class SaleNotFoundException extends RuntimeException {
		SaleNotFoundException() {
			super("No sale exists with the given id!");
		}
	}
}
