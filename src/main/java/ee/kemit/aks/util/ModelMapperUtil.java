package ee.kemit.aks.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ModelMapperUtil {

	private static final org.modelmapper.ModelMapper modelMapper;

	static {
		modelMapper = new org.modelmapper.ModelMapper();
	}

	private ModelMapperUtil() {
	}

	public static <D, T> D map(final T entity, Class<D> outClass) {
		return modelMapper.map(entity, outClass);
	}

	public static <D, T> List<D> mapAll(final Collection<T> entityList, Class<D> outCLass) {
		return entityList.stream().map(entity -> map(entity, outCLass)).collect(Collectors.toList());
	}
}



