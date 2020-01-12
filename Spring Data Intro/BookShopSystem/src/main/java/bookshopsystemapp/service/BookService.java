package bookshopsystemapp.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface BookService {
    void seedBooks() throws IOException;
    List<String> getAllBooksTitleAfter();
    Set<String> getAllAuthorsWithBookBefore();
}
