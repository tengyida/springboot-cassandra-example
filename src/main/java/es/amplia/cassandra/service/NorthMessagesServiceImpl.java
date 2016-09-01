package es.amplia.cassandra.service;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Session;
import es.amplia.cassandra.entity.NorthMessageByInterval;
import es.amplia.cassandra.entity.NorthMessageByInterval.NorthMessageByIntervalBuilder;
import es.amplia.cassandra.entity.NorthMessageByUserInterval;
import es.amplia.cassandra.entity.NorthMessageByUserInterval.NorthMessageByUserIntervalBuilder;
import es.amplia.cassandra.entity.NorthMessageByUserSubjectInterval;
import es.amplia.cassandra.entity.NorthMessageByUserSubjectInterval.NorthMessageByUserSubjectIntervalBuilder;
import es.amplia.cassandra.entity.Page;
import es.amplia.cassandra.repository.NorthMessagesByIntervalRepository;
import es.amplia.cassandra.repository.NorthMessagesByUserIntervalRepository;
import es.amplia.cassandra.repository.NorthMessagesByUserSubjectIntervalRepository;
import es.amplia.model.AuditMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NorthMessagesServiceImpl implements NorthMessagesService {

    private Session session;
    private NorthMessagesByIntervalRepository northMessagesByIntervalRepository;
    private NorthMessagesByUserIntervalRepository northMessagesByUserIntervalRepository;
    private NorthMessagesByUserSubjectIntervalRepository northMessagesByUserSubjectIntervalRepository;

    @Autowired
    public NorthMessagesServiceImpl(
            Session session,
            NorthMessagesByIntervalRepository northMessagesByIntervalRepository,
            NorthMessagesByUserIntervalRepository northMessagesByUserIntervalRepository,
            NorthMessagesByUserSubjectIntervalRepository northMessagesByUserSubjectIntervalRepository
    ) {
        this.session = session;
        this.northMessagesByIntervalRepository = northMessagesByIntervalRepository;
        this.northMessagesByUserIntervalRepository = northMessagesByUserIntervalRepository;
        this.northMessagesByUserSubjectIntervalRepository = northMessagesByUserSubjectIntervalRepository;
    }

    @Override
    public void save(AuditMessage message) {
        BatchStatement batch = new BatchStatement();
        batch.add(northMessagesByIntervalRepository.saveQuery((NorthMessageByInterval) NorthMessageByIntervalBuilder.builder().build(message)));
        batch.add(northMessagesByUserIntervalRepository.saveQuery((NorthMessageByUserInterval) NorthMessageByUserIntervalBuilder.builder().build(message)));
        batch.add(northMessagesByUserSubjectIntervalRepository.saveQuery((NorthMessageByUserSubjectInterval) NorthMessageByUserSubjectIntervalBuilder.builder().build(message)));
        session.execute(batch);
    }

    @Override
    public Page<NorthMessageByInterval> getMessagesByInterval(Date from, Date to, String pagingState) {
        return northMessagesByIntervalRepository.getMessagesByInterval(from, to, pagingState);
    }

    @Override
    public Page<NorthMessageByUserInterval> getMessagesByUserInterval(String user, Date from, Date to, String pagingState) {
        return northMessagesByUserIntervalRepository.getMessagesByUserAndInterval(user, from, to, pagingState);
    }

    @Override
    public Page<NorthMessageByUserSubjectInterval> getMessagesByUserSubjectInterval(String user, String subject, Date from, Date to, String pagingState) {
        return northMessagesByUserSubjectIntervalRepository.getMessagesByUserSubjectAndInterval(user, subject, from, to, pagingState);
    }
}
