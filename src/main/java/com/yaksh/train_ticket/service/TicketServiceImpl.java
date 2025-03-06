package com.yaksh.train_ticket.service;

import com.yaksh.train_ticket.model.Ticket;
import com.yaksh.train_ticket.repository.TicketRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
