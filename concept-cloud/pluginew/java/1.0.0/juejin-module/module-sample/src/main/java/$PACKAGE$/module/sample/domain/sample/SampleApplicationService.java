package $PACKAGE$.module.sample.domain.sample;

import $PACKAGE$.domain.sample.Sample;
import $PACKAGE$.domain.sample.SampleRepository;
import $PACKAGE$.domain.sample.SampleService;
import $PACKAGE$.domain.user.User;
import $PACKAGE$.module.sample.domain.sample.view.SampleCreateCommand;
import $PACKAGE$.module.sample.domain.sample.view.SampleDeleteCommand;
import com.github.linyuzai.domain.core.exception.DomainNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 沸点应用服务
 */
@Service
public class SampleApplicationService {

    @Autowired
    private SampleService sampleService;

    @Autowired
    private SampleFacadeAdapter sampleFacadeAdapter;

    @Autowired
    private SampleRepository sampleRepository;

    /**
     * 创建
     */
    public void create(SampleCreateCommand create, User user) {
        Sample sample = sampleFacadeAdapter.from(create, user);
        sampleService.create(sample, user);
    }

    /**
     * 删除
     */
    public void delete(SampleDeleteCommand delete, User user) {
        Sample sample = sampleRepository.get(delete.getId());
        if (sample == null) {
            throw new DomainNotFoundException(Sample.class, delete.getId());
        }
        sampleService.delete(sample, user);
    }
}
