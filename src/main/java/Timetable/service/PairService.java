package Timetable.service;

import Timetable.model.Pair;
import Timetable.repositories.PairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PairService {
    private final PairRepository pairRepository;

    @Autowired
    public PairService(PairRepository pairRepository) {
        this.pairRepository = pairRepository;
    }

    public Pair save(Pair pair) {
        return pairRepository.save(pair);
    }
}
