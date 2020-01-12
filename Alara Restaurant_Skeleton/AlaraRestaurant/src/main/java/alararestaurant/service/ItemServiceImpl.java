package alararestaurant.service;

import alararestaurant.domain.entities.Category;
import alararestaurant.domain.dtos.ItemImportDto;
import alararestaurant.domain.entities.Item;
import alararestaurant.repository.CategoryRepository;
import alararestaurant.repository.ItemRepository;
import alararestaurant.util.FileUtil;
import alararestaurant.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final FileUtil fileUtil;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final static String ITEMS_JSON_FILE_PATH =
            System.getProperty("user.dir") + ("\\src\\main\\resources\\files\\items.json");

    public ItemServiceImpl(ItemRepository itemRepository, CategoryRepository categoryRepository, FileUtil fileUtil, ValidationUtil validationUtil, ModelMapper modelMapper, Gson gson) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.fileUtil = fileUtil;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }

    @Override
    public Boolean itemsAreImported() {
        return this.itemRepository.count() > 0;
    }

    @Override
    public String readItemsJsonFile() throws IOException {
        return fileUtil.readFile(ITEMS_JSON_FILE_PATH);
    }

    @Override
    public String importItems(String items) {
        StringBuilder importResult = new StringBuilder();
        ItemImportDto[] itemImportDtos = this.gson.fromJson(items, ItemImportDto[].class);
        for (ItemImportDto itemImportDto : itemImportDtos) {
            if (!this.validationUtil.isValid(itemImportDto)) {
                importResult.append("Invalid data format.").append(System.lineSeparator());
                continue;
            }
            if ("".equals(itemImportDto.getCategory())) {
                importResult.append("Invalid data format.").append(System.lineSeparator());
                continue;
            }
            Item itemEntity = this.itemRepository.findByName(itemImportDto.getName()).orElse(null);
            if(itemEntity !=null){
                importResult.append("Invalid data format.").append(System.lineSeparator());
                continue;
            }
            itemEntity = this.modelMapper.map(itemImportDto, Item.class);
            itemEntity.setCategory(null);
            this.itemRepository.saveAndFlush(itemEntity);

            Category categoryEntity = this.categoryRepository.findByName(itemImportDto.getCategory()).orElse(null);
            if (categoryEntity == null) {
                String s= itemImportDto.getCategory();
                categoryEntity = this.modelMapper.map(s,Category.class);
                categoryEntity.setName(s);
                itemEntity.setCategory(categoryEntity);
                this.categoryRepository.saveAndFlush(categoryEntity);
                this.itemRepository.saveAndFlush(itemEntity);
            }else {
                itemEntity.setCategory(categoryEntity);
                this.itemRepository.saveAndFlush(itemEntity);
            }
            importResult.append(String.format("Record %s successfully imported.",itemEntity.getName())).append(System.lineSeparator());
        }
        return importResult.toString().trim();
    }
}
