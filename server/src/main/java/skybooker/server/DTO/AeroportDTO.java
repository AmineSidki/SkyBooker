package skybooker.server.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import skybooker.server.entity.Aeroport;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AeroportDTO {
    private Long id;

    @NotNull
    private String iataCode;

    @NotNull
    private String icaoCode;

    @NotNull
    private String nom;

    @NotNull
    private double latitude;

    @NotNull
    private double longitude;

    @NotNull
    private Long villeId;

    public AeroportDTO(Aeroport aeroport) {
        setId(aeroport.getId());
        setIataCode(aeroport.getIataCode());
        setIcaoCode(aeroport.getIcaoCode());
        setNom(aeroport.getNom());
        setLatitude(aeroport.getLatitude());
        setLongitude(aeroport.getLongitude());
        if (aeroport.getVille() != null) {
            setVilleId(aeroport.getVille().getId());
        } else {
            setVilleId(null);
        }
    }
}
