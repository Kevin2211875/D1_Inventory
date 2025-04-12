package com.uis.ejeMvc;

import com.uis.ejeMvc.repository.PersonaRepositoryInter;
import com.uis.ejeMvc.service.TipodocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class PersonaRepositoryTest {

    @Autowired
    private PersonaRepositoryInter personaRepositoryInter;
    private CiudadService ciudadService;
    private Ciudad ciudad =new Ciudad();
    private TipodocumentoService tipodocumentoService;


    /*
    @Test
    public void testCrearPersona(){
        Persona personaGuardada = personaRepositoryInter.save(new Persona(
                1L, "Jose", "Martines", "1092395932", "12/12/25", "josemar@gmail", "310778596", "josemar23", "1234", ciudadService.getById(1L), tipodocumentoService.getById(2L));
        assertThat(personaGuardada.getId()).isGreaterThan(0);
    }


     */




}
