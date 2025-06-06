package skybooker.server.service;

import skybooker.server.DTO.VolDTO;
import skybooker.server.entity.Vol;

import java.util.List;


public interface VolService extends CrudDTO<VolDTO, Long>, CrudService<Vol, Long> {
    Double calculatePrice(Long volId, Long classeId);
    List<VolDTO> getTrajetVols(Long villeDepartId, Long villeArriveeId);
}