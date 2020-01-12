package product_shop.persistance.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import product_shop.model.dto.binding.UserDto;
import product_shop.model.dto.view.*;
import product_shop.model.entity.User;
import product_shop.persistance.repository.UserRepository;
import product_shop.persistance.service.api.UserService;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository,
                           final ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void saveAll(final UserDto[] usersDto) {
        User[] users = this.modelMapper.map(usersDto, User[].class);
        this.userRepository.saveAll(Arrays.asList(users));

        for (int i = 0; i < users.length * 2; i++) {
            User user = this.userRepository.getRandomEntity();
            User friend = this.userRepository.getRandomEntity();
            if (user != null && friend != null && !user.equals(friend)) {
                user.getFriends().add(friend);
                friend.getFriends().add(user);
            }
        }
    }

    @Override
    public List<UserFirstAndLastNamesAndSoldProductsDto> getSuccessfulSellers() {
        return this.userRepository
                .getAllBySellContainsProduct_Buyer()
                .stream()
                .map(user -> {
                    final UserFirstAndLastNamesAndSoldProductsDto userDto =
                            this.modelMapper.map(user, UserFirstAndLastNamesAndSoldProductsDto.class);
                    userDto.setSoldProducts(user
                            .getSell()
                            .stream()
                            .filter(sale -> sale.getBuyer() != null)
                            .map(sale -> this.modelMapper.map(sale, ProductNamePriceBuyerFirstAndLastNamesDto.class))
                            .collect(Collectors.toSet()));
                    return userDto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public UsersWithSalesListDto getSellsByUser() {
        final List<UserFirstLastNamesAgeAndSoldProductsNameAndPriceDto> users = this.userRepository
                .getAllBySellContainsProduct_Buyer()
                .stream()
                .map(user -> {
                    final UserFirstLastNamesAgeAndSoldProductsNameAndPriceDto userDto =
                            this.modelMapper.map(user, UserFirstLastNamesAgeAndSoldProductsNameAndPriceDto.class);

                    final SoldProductsDto soldProductsDto = new SoldProductsDto();

                    soldProductsDto.setSoldProducts(user
                            .getSell()
                            .stream()
                            .filter(sale -> sale.getBuyer() != null)
                            .map(sale -> this.modelMapper.map(sale, ProductNameAndPriceDto.class))
                            .collect(Collectors.toSet()));

                    soldProductsDto.setCount(soldProductsDto.getSoldProducts().size());

                    userDto.setSoldProducts(soldProductsDto);

                    return userDto;
                })
                .sorted((u1, u2) -> {
                    int cmp = u2.getSoldProducts().getCount() - u1.getSoldProducts().getCount();
                    if (cmp == 0) {
                        cmp = u1.getLastName().compareTo(u2.getLastName());
                    }
                    return cmp;
                })
                .collect(Collectors.toList());

        final UsersWithSalesListDto usersWithSalesListDto = new UsersWithSalesListDto();
        usersWithSalesListDto.setUsers(users);
        usersWithSalesListDto.setUsersCount(users.size());

        return usersWithSalesListDto;
    }

    @Override
    public User getRandom() {
        return this.userRepository.getRandomEntity();
    }
}
