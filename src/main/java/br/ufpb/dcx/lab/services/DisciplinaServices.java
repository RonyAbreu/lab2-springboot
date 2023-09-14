package br.ufpb.dcx.lab.services;

import br.ufpb.dcx.lab.dto.DisciplinaDTO;
import br.ufpb.dcx.lab.dto.NotaDTO;
import br.ufpb.dcx.lab.entities.Disciplina;
import br.ufpb.dcx.lab.repository.DisciplinaDAORepository;
import br.ufpb.dcx.lab.services.exceptions.DisciplinaAlreadyExistsException;
import br.ufpb.dcx.lab.services.exceptions.DisciplinaNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DisciplinaServices {
    @Autowired
    private DisciplinaDAORepository repository;

    public void insertDiscipline(Disciplina obj) {
        Optional<Disciplina> discipline = repository.findByName(obj.getName());

        if (discipline.isPresent()){
            throw new DisciplinaAlreadyExistsException("Essa disciplina já foi cadastrada!");
        }

        repository.save(obj);
    }

    public Disciplina findById(Long id) throws DisciplinaNotFound {
        Optional<Disciplina> discipline = repository.findById(id);

        return discipline.orElseThrow(() -> new DisciplinaNotFound("Não foi encontrada disciplina com esse id: "+ id));
    }

    public List<Disciplina> findAll() {
        List<Disciplina> list = repository.findAll();

        if (list.size() == 0){
            throw new DisciplinaNotFound("Lista de disciplinas está vazia!");
        }

        return repository.findAll();
    }

    public void deleteDiscipline(Long id) {
        Optional<Disciplina> discipline = Optional.ofNullable(findById(id));

        if (discipline.isEmpty()){
            throw new DisciplinaNotFound("Não foi encontrada disciplina com esse id: "+ id);
        }

        repository.deleteById(id);
    }

    public Disciplina updateDiscipline(Disciplina discipline, Long id) {
        Optional<Disciplina> newDiscipline = repository.findById(id);

        if (newDiscipline.isEmpty()){
            throw new DisciplinaNotFound("Não foi encontrada disciplina com esse id: "+ id);
        }

        updateDiscipline(newDiscipline.get(), discipline);
        return repository.save(newDiscipline.get());
    }

    private void updateDiscipline(Disciplina newDiscipline, Disciplina discipline) {
        newDiscipline.setName(discipline.getName());
    }

    public Disciplina insertNota(Long id, NotaDTO note){
        Optional<Disciplina> newDiscipline = repository.findById(id);

        if (newDiscipline.isEmpty()){
            throw new DisciplinaNotFound("Não foi encontrada disciplina com esse id: "+ id);
        }

        newDiscipline.get().adicionaNotas(note.getNote());
        return repository.save(newDiscipline.get());
    }

    public Disciplina insertLike(Long id){
        Optional<Disciplina> newDiscipline = repository.findById(id);

        if (newDiscipline.isEmpty()){
            throw new DisciplinaNotFound("Não foi encontrada disciplina com esse id: "+ id);
        }

        newDiscipline.get().somaLikes();
        return repository.save(newDiscipline.get());
    }

    public List<Disciplina> findRankingNotas(){
        List<Disciplina> list = repository.findAll();

        if (list.size() == 0){
            throw new DisciplinaNotFound("Lista de disciplinas está vazia!");
        }

        list.sort(Comparator.comparingDouble(Disciplina::calculaMedia).reversed());
        return list;
    }

    public List<Disciplina> findRankingLikes(){
        List<Disciplina> list = repository.findAll();

        if (list.size() == 0){
            throw new DisciplinaNotFound("Lista de disciplinas está vazia!");
        }

        list.sort(Comparator.comparingInt(Disciplina::getLikes).reversed());
        return list;
    }

    public Disciplina dtoFromDiscipline(DisciplinaDTO disciplineDto){
        return new Disciplina(null, disciplineDto.getName(),0);
    }
}

