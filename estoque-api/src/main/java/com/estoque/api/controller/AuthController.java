package com.estoque.api.controller;

import com.estoque.api.entity.Erole;
import com.estoque.api.entity.Role;
import com.estoque.api.entity.User;
import com.estoque.api.payload.request.LoginRequest;
import com.estoque.api.payload.request.SignupRequest;
import com.estoque.api.payload.response.JwtResponse;
import com.estoque.api.payload.response.MessageResponse;
import com.estoque.api.repository.RoleRepository;
import com.estoque.api.repository.UserRepository;
import com.estoque.api.security.jwt.JwtUtils;
import com.estoque.api.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    // 1. ENDPOINT DE LOGIN
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // 1. Autentica o usuário usando o AuthenticationManager (que usa o UserDetailsServiceImpl)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. Define a autenticação no contexto de segurança
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Gera o token JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 4. Converte os detalhes do usuário e os roles para a resposta
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // 5. Retorna o token e os detalhes do usuário
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles));
    }

    // 2. ENDPOINT DE REGISTRO
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Verifica se o username já está em uso
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Erro: Username já está em uso!"));
        }

        // 1. Cria nova conta de usuário
        User user = new User(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            // Se nenhum perfil for especificado, define o perfil padrão (USER)
            Role userRole = roleRepository.findByName(Erole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Erro: Perfil (Role) não encontrado."));
            roles.add(userRole);
        } else {
            // Mapeia os perfis fornecidos
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(Erole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Erro: Perfil (Role) ADMIN não encontrado."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(Erole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Erro: Perfil (Role) MODERATOR não encontrado."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(Erole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Erro: Perfil (Role) USER não encontrado."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Usuário registrado com sucesso!"));
    }
}