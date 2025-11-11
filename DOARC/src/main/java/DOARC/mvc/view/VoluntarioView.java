package DOARC.mvc.view;

import DOARC.mvc.controller.VoluntarioController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/voluntario")
public class VoluntarioView {

    @Autowired
    private VoluntarioController voluntarioController;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<Map<String, Object>> lista = voluntarioController.getVoluntarios();

        return ResponseEntity.ok(lista);
    }
}