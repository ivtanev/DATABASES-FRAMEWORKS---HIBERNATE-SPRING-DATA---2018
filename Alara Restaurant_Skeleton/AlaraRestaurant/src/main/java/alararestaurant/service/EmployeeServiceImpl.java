package alararestaurant.service;

import alararestaurant.domain.dtos.EmployeeImportDto;
import alararestaurant.domain.entities.Employee;
import alararestaurant.domain.entities.Position;
import alararestaurant.repository.EmployeeRepository;
import alararestaurant.repository.PositionRepository;
import alararestaurant.util.FileUtil;
import alararestaurant.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final FileUtil fileUtil;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final static String EMPLOYEES_JSON_FILE_PATH =
            System.getProperty("user.dir") + ("\\src\\main\\resources\\files\\employees.json");

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, PositionRepository positionRepository, FileUtil fileUtil, ValidationUtil validationUtil, Gson gson, ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
        this.fileUtil = fileUtil;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.modelMapper = modelMapper;
    }

    @Override
    public Boolean employeesAreImported() {
        return this.employeeRepository.count() > 0;
    }

    @Override
    public String readEmployeesJsonFile() throws IOException {
        return fileUtil.readFile(EMPLOYEES_JSON_FILE_PATH);
    }

    @Override
    public String importEmployees(String employees) {
        StringBuilder importResult = new StringBuilder();
        EmployeeImportDto[] employeeImportDtos = this.gson.fromJson(employees, EmployeeImportDto[].class);
        for (EmployeeImportDto employeeImportDto : employeeImportDtos) {
            if (!this.validationUtil.isValid(employeeImportDto)) {
                importResult.append("Invalid data format.").append(System.lineSeparator());
                continue;
            }
            if ("".equals(employeeImportDto.getPosition())) {
                importResult.append("Invalid data format.").append(System.lineSeparator());
                continue;
            }

            Employee employeeEntity = this.modelMapper.map(employeeImportDto, Employee.class);
            employeeEntity.setPosition(null);
            this.employeeRepository.saveAndFlush(employeeEntity);

            Position positionEntity = this.positionRepository.findByName(employeeImportDto.getPosition()).orElse(null);
            if (positionEntity == null) {
                String s= employeeImportDto.getPosition();
                positionEntity = this.modelMapper.map(s,Position.class);
                positionEntity.setName(s);
                employeeEntity.setPosition(positionEntity);
                this.positionRepository.saveAndFlush(positionEntity);
                this.employeeRepository.saveAndFlush(employeeEntity);
            }else {
                employeeEntity.setPosition(positionEntity);
                this.employeeRepository.saveAndFlush(employeeEntity);
            }
            importResult.append(String.format("Record %s successfully imported.",employeeEntity.getName())).append(System.lineSeparator());

        }
        return importResult.toString().trim();
    }
}
