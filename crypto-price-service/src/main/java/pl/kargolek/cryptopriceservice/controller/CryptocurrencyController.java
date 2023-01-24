package pl.kargolek.cryptopriceservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kargolek.cryptopriceservice.dto.controller.CryptocurrencyPostDTO;
import pl.kargolek.cryptopriceservice.dto.model.CryptocurrencyDTO;
import pl.kargolek.cryptopriceservice.mapper.CryptocurrencyMapper;
import pl.kargolek.cryptopriceservice.mapper.util.CycleAvoidingMappingContext;
import pl.kargolek.cryptopriceservice.service.CryptocurrencyService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * @author Karol Kuta-Orlowicz
 */

@RequestMapping(path = "api/v1/cryptocurrency")
@RestController
@Slf4j
public class CryptocurrencyController {

    private static final CryptocurrencyMapper mapper = CryptocurrencyMapper.INSTANCE;
    @Autowired
    private CryptocurrencyService cryptocurrencyService;

    @GetMapping("/{id}")
    public ResponseEntity<CryptocurrencyDTO> getCryptocurrencyById(@PathVariable("id") Long id) {
        var cryptocurrency = cryptocurrencyService.getById(id);
        var cryptocurrencyDto = mapper.mapEntityToCryptocurrencyDto(cryptocurrency, new CycleAvoidingMappingContext());
        return ResponseEntity.status(HttpStatus.OK).body(cryptocurrencyDto);
    }

    @GetMapping("")
    public List<CryptocurrencyDTO> getCryptocurrencies(@RequestParam(name = "name", required = false) List<String> names) {
        return Optional.ofNullable(names).isPresent() ?
                cryptocurrencyService.getByName(names)
                        .stream()
                        .map(cryptocurrency -> mapper.mapEntityToCryptocurrencyDto(cryptocurrency, new CycleAvoidingMappingContext()))
                        .toList() :
                cryptocurrencyService.getCryptocurrencies()
                        .stream()
                        .map(cryptocurrency -> mapper.mapEntityToCryptocurrencyDto(cryptocurrency, new CycleAvoidingMappingContext()))
                        .toList();
    }

    @PostMapping("")
    public ResponseEntity<CryptocurrencyDTO> registerCryptocurrency(
            @Valid @RequestBody CryptocurrencyPostDTO cryptocurrencyPostDTO) {
        var registerCryptocurrency = mapper.mapPostDtoToCryptocurrencyEntity(cryptocurrencyPostDTO);
        var cryptocurrency = cryptocurrencyService.addCryptocurrency(registerCryptocurrency);
        var cryptocurrencyDTO = mapper.mapEntityToCryptocurrencyDto(cryptocurrency, new CycleAvoidingMappingContext());
        return ResponseEntity.status(HttpStatus.CREATED).body(cryptocurrencyDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteCryptocurrencyById(@PathVariable("id") Long id) {
        cryptocurrencyService.deleteCryptocurrency(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CryptocurrencyDTO> updateCryptocurrency(@PathVariable("id") Long id,
                                                                  @Valid @RequestBody CryptocurrencyPostDTO cryptocurrencyPostDTO) {
        var cryptocurrencyUpdate = mapper.mapPostDtoToCryptocurrencyEntity(cryptocurrencyPostDTO);
        var cryptocurrency = cryptocurrencyService.updateCryptocurrency(id, cryptocurrencyUpdate);
        var cryptocurrencyDto = mapper.mapEntityToCryptocurrencyDto(cryptocurrency, new CycleAvoidingMappingContext());
        return ResponseEntity.status(HttpStatus.OK).body(cryptocurrencyDto);
    }

}
