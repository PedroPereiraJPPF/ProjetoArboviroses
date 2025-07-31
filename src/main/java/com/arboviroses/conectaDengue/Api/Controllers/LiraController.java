package com.arboviroses.conectaDengue.Api.Controllers;

import com.arboviroses.conectaDengue.Domain.Entities.Lira.Lira;
import com.arboviroses.conectaDengue.Domain.Services.Lira.LiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/lira")
@RequiredArgsConstructor
public class LiraController {

    private final LiraService liraService;

    @PostMapping("/upload")
    public ResponseEntity<List<Lira>> uploadLiraFile(@RequestParam("file") MultipartFile file, @RequestParam("ano") Integer ano) {
        try {
            List<Lira> savedData = liraService.saveLiraData(file, ano);
            return ResponseEntity.ok(savedData);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Lira>> getLiraByAno(@RequestParam("ano") Integer ano) {
        List<Lira> liraData = liraService.getLiraByAno(ano);
        return ResponseEntity.ok(liraData);
    }
}
