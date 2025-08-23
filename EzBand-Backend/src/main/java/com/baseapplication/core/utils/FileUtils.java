package com.baseapplication.core.utils;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

	public static String getSufix(MultipartFile multipartFile) {
		String fileName = multipartFile.getOriginalFilename();
		String[] splitted = fileName.split("\\.");

		return Optional.ofNullable(splitted).filter(arr -> arr.length > 0).map(arr -> arr[arr.length - 1]).orElse(null);
	}

}
