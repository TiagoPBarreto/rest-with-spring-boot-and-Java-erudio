package br.com.barreto.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.barreto.config.FileStorageConfig;
import br.com.barreto.exception.FileStorageException;
import br.com.barreto.exception.MyFileNotFouldException;
import io.github.classgraph.Resource;

@Service
public class FileStorageService {

	private final Path fileStorageLocation;

	@Autowired
	public FileStorageService(FileStorageConfig fileStorageConfig) {
		Path path = Paths.get(fileStorageConfig.getUploadDir())
				.toAbsolutePath().normalize();
		
		this.fileStorageLocation = path;
		
		try {
			
			Files.createDirectories(this.fileStorageLocation);
			
		} catch (Exception e) {
			throw new FileStorageException("Could not create the directory where the upload files will be stored",e);
		}
	}
	
	public String storeFile(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			//arquivo pode esta com nome errado, então pode-se fazer a validação dele 
			if(filename.contains("..")) {
				throw new FileStorageException("Sorry! Filenaem constains invalid path sequence");
			}
			Path targetLocation = this.fileStorageLocation.resolve(filename);
			Files.copy(file.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return filename;
		} catch (Exception e) {
			throw new FileStorageException("Could not store fiel "+ filename +". Please try again",e);
		}
	}
	
	public org.springframework.core.io.Resource loadFileAsResource(String filename) {
		try {
			Path filePath = this.fileStorageLocation.resolve(filename).normalize();
			UrlResource resource = new UrlResource(filePath.toUri());
			if(resource.exists()) return resource;
			else throw new MyFileNotFouldException("File not found");
		} catch (Exception e) {
			throw new MyFileNotFouldException("File not found "+filename, e);
		}
		
	}
}
