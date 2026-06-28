package com.vamshi.stockflow_backend.analytics.service;

import com.vamshi.stockflow_backend.common.exception.ResourceNotFoundException;
import com.vamshi.stockflow_backend.user.domain.User;
import com.vamshi.stockflow_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Resolves which warehouse the currently authenticated user is allowed to see
 * analytics for. Admins are unrestricted (returns {@code null} = all warehouses);
 * warehouse managers are scoped to their own assigned warehouse.
 */
@Component
@RequiredArgsConstructor
public class AnalyticsScopeResolver {

    private final UserRepository userRepository;

    /**
     * @return {@code null} for admins (no warehouse restriction), otherwise the
     *         id of the warehouse the current manager is assigned to.
     */
    public UUID resolveWarehouseFilter() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        if (isAdmin) {
            return null;
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", authentication.getName()));

        if (user.getWarehouse() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Manager is not assigned to a warehouse");
        }

        return user.getWarehouse().getId();
    }
}
