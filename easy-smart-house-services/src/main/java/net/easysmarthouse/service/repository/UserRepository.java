package net.easysmarthouse.service.repository;

import net.easysmarthouse.shared.domain.user.User;

public interface UserRepository {

    public User save(User user);

    public User findByUsername(String username);

    public User findById(Long id);

    public User update(User user);

}
