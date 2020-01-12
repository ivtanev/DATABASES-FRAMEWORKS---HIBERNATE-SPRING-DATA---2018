package alararestaurant.service;

import alararestaurant.domain.dtos.OrderImportDto;
import alararestaurant.domain.dtos.OrderImportRootDto;
import alararestaurant.domain.entities.Employee;
import alararestaurant.domain.entities.Order;
import alararestaurant.repository.EmployeeRepository;
import alararestaurant.repository.OrderRepository;
import alararestaurant.util.FileUtil;
import alararestaurant.util.ValidationUtil;
import alararestaurant.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;
    private final FileUtil fileUtil;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final static String ORDERS_XML_FILE_PATH =
            System.getProperty("user.dir") + ("\\src\\main\\resources\\files\\orders.xml");

    public OrderServiceImpl(OrderRepository orderRepository, EmployeeRepository employeeRepository, FileUtil fileUtil, ValidationUtil validationUtil, ModelMapper modelMapper, XmlParser xmlParser) {
        this.orderRepository = orderRepository;
        this.employeeRepository = employeeRepository;
        this.fileUtil = fileUtil;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
    }

    @Override
    public Boolean ordersAreImported() {
        return this.orderRepository.count() > 0;
    }

    @Override
    public String readOrdersXmlFile() throws IOException {
        return this.fileUtil.readFile(ORDERS_XML_FILE_PATH);
    }

    @Override
    public String importOrders() throws FileNotFoundException, JAXBException {
        StringBuilder importResult = new StringBuilder();
        OrderImportRootDto orderImportRootDto = xmlParser.parseXml(OrderImportRootDto.class,ORDERS_XML_FILE_PATH);
        for (OrderImportDto orderImportDto : orderImportRootDto.getOrderImportDtos()) {
            Employee employeeEntity = this.employeeRepository.findByName(orderImportDto.getEmployee()).orElse(null);
            if(employeeEntity == null){
                importResult.append("Invalid data format.").append(System.lineSeparator());
                continue;
            }
            if(!this.validationUtil.isValid(orderImportDto)){
                importResult.append("Invalid data format.").append(System.lineSeparator());
                continue;
            }
            Order orderEntity = this.modelMapper.map(orderImportDto,Order.class);
            orderEntity.setEmployee(employeeEntity);
            orderEntity.setDateTime(LocalDate.parse(orderImportDto.getDateTime(),DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            this.orderRepository.saveAndFlush(orderEntity);
            importResult.append(String.format("Order for %s on %s added",employeeEntity.getName(),orderEntity.getDateTime()))
                    .append(System.lineSeparator());
        }

        return importResult.toString().trim();
    }

    @Override
    public String exportOrdersFinishedByTheBurgerFlippers() {
        // TODO : Implement me
        return null;
    }
}
