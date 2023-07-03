package ee.kemit.aks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ee.kemit.aks.dto.ClassifierResponseDto;
import ee.kemit.aks.repository.CountyRepository;
import ee.kemit.aks.repository.MunicipalityRepository;
import ee.kemit.aks.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ClassifierService {

	private final CountyRepository countyRepository;
	private final MunicipalityRepository municipalityRepository;

	public List<ClassifierResponseDto> getCounties() {
		return ModelMapperUtil.mapAll(countyRepository.findAll(), ClassifierResponseDto.class);
	}

	public List<ClassifierResponseDto> getMunicipalities() {
		return ModelMapperUtil.mapAll(municipalityRepository.findAll(), ClassifierResponseDto.class);
	}
}
