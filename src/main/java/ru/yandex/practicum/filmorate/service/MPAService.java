package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;
import ru.yandex.practicum.filmorate.utility.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MPAService {

    @Autowired
    private final MPAStorage storage;

    public MPA getMPA(int id) {
        Optional<MPA> mpa = storage.getMPA(id);
        if (mpa.isEmpty()) {
            throw new EntityNotFoundException("MPA not found");
        }
        return mpa.get();
    }

    public List<MPA> getAllMPA() {
        return storage.getAllMPA();
    }
}