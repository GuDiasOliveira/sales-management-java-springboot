package com.github.gudiasoliveira.SellingsManagement.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.gudiasoliveira.SellingsManagement.models.Foo;

@RestController
@RequestMapping("/foo/")
public class FooController {
	
	private List<Foo> mFoos = new ArrayList<>();
	private long mIdCount = 0L;
	
	@RequestMapping("/hello-world.html")
	String helloWorld() {
		return "Hello world!!!";
	}
	
	@GetMapping("/")
	List<Foo> getFoos(
			@RequestParam(value = "q", defaultValue = "") String query
			) {
		query = query.trim();
		if (query.isEmpty())
			return mFoos;
		List<Foo> queryFoos = new ArrayList<>();
		for (Foo foo : mFoos) {
			try {
				if (foo.getName().contains(query) || foo.getAge() == Integer.parseInt(query))
					queryFoos.add(foo);
			} catch (NumberFormatException ignored) {
			}
		}
		return queryFoos;
	}
	
	@GetMapping("/{id}")
	Foo getFooById(@PathVariable(value = "id") long id) {
		Foo foo = null;
		for (Foo f : mFoos) {
			if (f.getId() == id) {
				foo = f;
				break;
			}
		}
		if (foo == null)
			throw new FooNotFoundException();
		return foo;
	}
	
	@PostMapping("/")
	String createFoo(@Valid @RequestBody Foo foo) {
		foo.setId(++mIdCount);
		mFoos.add(foo);
		return "Foo created!!";
	}
	
	@SuppressWarnings("serial")
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	static class FooNotFoundException extends RuntimeException {
		FooNotFoundException() {
			super("The Foo does not exist!");
		}
	}
}
