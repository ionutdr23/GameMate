package nl.fhict.gamemate.userservice.controller;

import nl.fhict.gamemate.userservice.model.Platform;
import nl.fhict.gamemate.userservice.model.Playstyle;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/user/meta")
public class MetadataController {
    @GetMapping("/platforms")
    public ResponseEntity<List<Platform>> getPlatforms() {
        return ResponseEntity.ok(Arrays.asList(Platform.values()));
    }

    @GetMapping("/playstyles")
    public ResponseEntity<List<Playstyle>> getPlaystyles() {
        return ResponseEntity.ok(Arrays.asList(Playstyle.values()));
    }
}
