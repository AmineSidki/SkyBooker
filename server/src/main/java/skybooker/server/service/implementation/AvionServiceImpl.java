package skybooker.server.service.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skybooker.server.DTO.AvionDTO;
import skybooker.server.controller.AvionController;
import skybooker.server.entity.Avion;
import skybooker.server.entity.CompanieAerienne;
import skybooker.server.exception.NotFoundException;
import skybooker.server.repository.AvionRepository;
import skybooker.server.repository.CompanieAerienneRepository;
import skybooker.server.service.AvionService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AvionServiceImpl implements AvionService {

    private final CompanieAerienneRepository companieAerienneRepository;
    Logger logger = LoggerFactory.getLogger(AvionController.class);

    private final AvionRepository avionRepository;

    public AvionServiceImpl(AvionRepository avionRepository, CompanieAerienneRepository companieAerienneRepository) {
        this.avionRepository = avionRepository;
        this.companieAerienneRepository = companieAerienneRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "avionCache", key = "#id")
    public AvionDTO findDTOById(Long id) {
        Optional<Avion> avion = avionRepository.findById(id);
        return avion
                .map(AvionDTO::new)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    @CacheEvict(value = "avionCache", key = "#id")
    public void deleteById(Long id) {
        avionRepository.deleteById(id);
    }


    @Override
    public List<AvionDTO> findAllDTO() {
        return avionRepository.findAll()
                .stream().map(AvionDTO::new).toList();
    }

    @Override
    @CachePut(value = "avionCache", key = "#result.id")
    public AvionDTO createDTO(AvionDTO avionDTO) {
        Avion avion = new Avion(avionDTO);
        CompanieAerienne companieAerienne= companieAerienneRepository.findById(avionDTO.getCompanieAerienneId())
                .orElseThrow(() -> new NotFoundException("Companie arienne not found"));
        avion.setCompanieAerienne(companieAerienne);
        return new AvionDTO(avionRepository.save(avion));
    }

    @Override
    @CachePut(value = "avionCache", key = "#avionDTO.id")
    public AvionDTO updateDTO(AvionDTO avionDTO) {
        Avion avion = avionRepository.findById(avionDTO.getId())
                .orElseThrow(() -> new NotFoundException("Avion not found"));
        // getting the companie arienne
        CompanieAerienne companieAerienne = companieAerienneRepository.findById(avionDTO.getCompanieAerienneId())
                .orElseThrow(() -> new NotFoundException("Companie arienne not found"));
        avion.setCompanieAerienne(companieAerienne);
        avion.setModel(avionDTO.getModel());
        avion.setIcaoCode(avionDTO.getIcaoCode());
        avion.setIataCode(avionDTO.getIataCode());
        avion.setMaxDistance(avionDTO.getMaxDistance());
        // saving the avion
        return new AvionDTO(avionRepository.save(avion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avion> findAll() {
        return avionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Avion findById(Long id) {
        Optional<Avion> avion = avionRepository.findById(id);
        return avion.orElse(null);
    }

    @Override
    public Avion create(Avion avion) {
        AvionDTO avionDTO = new AvionDTO(avion);
        avionDTO = createDTO(avionDTO);
        return avionRepository.findById(avionDTO.getId())
                .orElseThrow(() -> new NotFoundException("Avion not found"));
    }

    @Override
    public Avion update(Avion avion) {
        AvionDTO avionDTO = new AvionDTO(avion);
        avionDTO = updateDTO(avionDTO);
        return avionRepository.findById(avionDTO.getId())
                .orElseThrow(() -> new NotFoundException("Avion not found"));
    }

    @Override
    public void delete(Avion avion) {
        deleteById(avion.getId());
    }
}
