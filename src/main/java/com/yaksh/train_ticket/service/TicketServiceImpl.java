package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.DTO.ResponseDataDTO;
import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.repository.TicketRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService{

    private final TicketRepositoryV2 ticketRepositoryV2;

    @Override
    public Ticket saveTicket(Ticket ticketToSave) {
        try{
            Ticket ticket =  ticketRepositoryV2.save(ticketToSave);
            if(ticket!=null){
                log.info("Ticket save successfully with id: {}",ticket.getTicketId());
                return ticket;

            }else{
                log.info("Error while saving ticket: ticket is null");
                return null;
            }
        }catch (Exception e){
            log.info("Error while saving ticket: {}",e.getMessage());
            return null;
        }

    }

    @Override
    public Optional<Ticket> findTicketById(String idOfTicketToFind) {
        return ticketRepositoryV2.findById(idOfTicketToFind);
    }

    @Override
    public void deleteTicketById(String idOfTicketToDelete) {
        ticketRepositoryV2.deleteById(idOfTicketToDelete);
    }

    @Override
    public Ticket createNewTicket(String userId, String trainPrn, LocalDate dateOfTravel, String source, String destination, List<List<Integer>> availableSeatsList) {
        Ticket ticket = new Ticket(
                UUID.randomUUID().toString(),
                userId,
                trainPrn,
                dateOfTravel,
                source,
                destination,
                availableSeatsList
        );
        return saveTicket(ticket);

    }
}
