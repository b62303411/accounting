package com.sam.accounting.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.sam.accounting.model.entities.Rates;

/**
 * Year Quebec Small Business Tax Rate 2016-2018 8 % 28 Mar - 31 Dec 2018 7 %
 * 2019 6 % 2020 5 % 1 Jan - 25 Mar 2021 4 % 26 Mar 2021 - 2023 3.2%
 */
@Service
public class SmallBusinessTaxRateService {

	public static double getQuebecProvincialRate(LocalDate date) {
		if (date.isBefore(LocalDate.of(2018, 3, 28))) {
			return 8.0;
		} else if (date.isBefore(LocalDate.of(2019, 1, 1))) {
			return 7.0;
		} else if (date.isBefore(LocalDate.of(2020, 1, 1))) {
			return 6.0;
		} else if (date.isBefore(LocalDate.of(2021, 1, 1))) {
			return 5.0;
		} else if (date.isBefore(LocalDate.of(2021, 3, 26))) {
			return 4.0;
		} else {
			return 3.2;
		}
	}

	public Rates getRates(Date date) {
		Rates rate = new Rates();
		rate.setFederal(9.0);
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		double prov = getQuebecProvincialRate(localDate );
		rate.setProvintial(prov);
		return rate;
	}

}
