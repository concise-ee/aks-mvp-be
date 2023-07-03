package ee.kemit.aks.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ee.kemit.aks.dto.ClassifierResponseDto;
import ee.kemit.aks.service.ClassifierService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("classifier")
@RequiredArgsConstructor
public class ClassifierController {

	private final ClassifierService classifierService;

	@GetMapping("/counties")
	public List<ClassifierResponseDto> getCounties() {
		return classifierService.getCounties();
	}

	@GetMapping("/municipalities")
	public List<ClassifierResponseDto> getMunicipalities() {
		return classifierService.getMunicipalities();
	}
}
