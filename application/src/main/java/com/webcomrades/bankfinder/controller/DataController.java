package com.webcomrades.bankfinder.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.webcomrades.bankfinder.datafetcher.DataFetcher;
import com.webcomrades.bankfinder.datafetcher.DataFetcher.ResponseHandler;
import com.webcomrades.bankfinder.model.Bank;
import com.webcomrades.bankfinder.model.Brand;

/**
 * @author Jo Somers - sayhello@josomers.be
 * @since 2013
 */

public class DataController {

	private final String bankPath;
	private final String brandPath;
	private final DataFetcher dataFetcher;
	
	public DataController(String bankPath, String brandPath, DataFetcher dataFetcher) {
		this.bankPath = bankPath;
		this.brandPath = brandPath;
		this.dataFetcher = dataFetcher;
	}
	
	public List<Bank> getBanks() throws IOException {	
		return DataParser.parseBanks(dataFetcher.get(new ResponseHandler() {
			@Override
			public String handleResponse(InputStream input) throws IOException {
				return IOUtils.toString(input);
			}
		}, bankPath));
	}
	
	public List<Brand> getBrands() throws IOException {
		return DataParser.parseBrands(dataFetcher.get(new ResponseHandler() {
			@Override
			public String handleResponse(InputStream input) throws IOException {
				return IOUtils.toString(input);
			}
		}, brandPath));
	}

	public Bank postBank(Bank mNewBank) throws IOException {
		return DataParser.parseBank(dataFetcher.post(new ResponseHandler() {
			@Override
			public String handleResponse(InputStream input) throws IOException {
				return IOUtils.toString(input);
			}
		}, bankPath, new Gson().toJson(mNewBank)));
	}
	
}
