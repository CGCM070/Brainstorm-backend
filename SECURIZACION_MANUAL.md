# Sistema de Securización Manual con Tokens

**Cesar Castillo**

Este proyecto implementa un **sistema de autenticación manual basado en tokens** para entender **cómo funciona la seguridad por debajo del capó**. Es una implementación simplificada que muestra los conceptos fundamentales que frameworks como Spring Security manejan automáticamente.

> ⚠️ **Nota importante:** Este es un sistema educativo/prototipo. Para producción, se recomienda usar Spring Security.

### Características Principales
- ✅ Autenticación basada en tokens UUID
- ✅ Validación mediante interceptores HTTP
- ✅ Anotaciones personalizadas (`@RequiresAuth`)
- ✅ Gestión de sesiones en memoria (ConcurrentHashMap)
- ✅ Integración automática de headers desde el frontend

---

## Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND (Angular)                       │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  AuthService                                                │ │
│  │  - Gestiona usuario, userId y token en localStorage        │ │
│  │  - Proporciona métodos register(), logout(), etc.          │ │
│  └──────────────────────┬──────────────────────────────────────┘ │
│                         │                                         │
│  ┌──────────────────────▼──────────────────────────────────────┐ │
│  │  AuthInterceptor (HttpInterceptorFn)                        │ │
│  │  - Intercepta TODAS las peticiones HTTP                     │ │
│  │  - Añade headers: User-Id y User-Token automáticamente     │ │
│  └──────────────────────┬──────────────────────────────────────┘ │
└────────────────────────┼────────────────────────────────────────┘
                         │ HTTP Request
                         │ Headers: User-Id, User-Token
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                         BACKEND (Spring Boot)                    │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  WebMvcConfig                                                │ │
│  │  - Registra el SecurityInterceptor en /v1/api/**           │ │
│  └──────────────────────┬───────────────────────────────────────┘ │
│                         │                                         │
│  ┌──────────────────────▼───────────────────────────────────────┐ │
│  │  SecurityInterceptor (HandlerInterceptor)                   │ │
│  │  - Intercepta peticiones a controladores                    │ │
│  │  - Busca anotación @RequiresAuth en el método              │ │
│  │  - Extrae User-Id y User-Token de headers                  │ │
│  │  - Valida con SessionTokenService                          │ │
│  └──────────────────────┬───────────────────────────────────────┘ │
│                         │                                         │
│  ┌──────────────────────▼───────────────────────────────────────┐ │
│  │  SessionTokenService                                        │ │
│  │  - Map<Long, String> userTokens (ConcurrentHashMap)        │ │
│  │  - generateTokenForUser(userId) → UUID                     │ │
│  │  - validateToken(userId, token) → boolean                  │ │
│  └──────────────────────┬───────────────────────────────────────┘ │
│                         │                                         │
│  ┌──────────────────────▼───────────────────────────────────────┐ │
│  │  @RequiresAuth                                              │ │
│  │  - Anotación personalizada en métodos de controladores     │ │
│  │  - Marca endpoints que requieren autenticación             │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  Controladores (UserController, RoomController, etc.)      │ │
│  │  - @RequiresAuth aplicado selectivamente                   │ │
│  │  - Endpoints protegidos: PUT, DELETE                       │ │
│  │  - Endpoints públicos: GET, POST (registro)                │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## Componentes del Backend

### 1. `@RequiresAuth` - Anotación Personalizada

Marca métodos que requieren autenticación (equivalente a `@PreAuthorize` de Spring Security):

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAuth { }

// Uso:
@PutMapping("/{id}")
@RequiresAuth  // ← Protege este endpoint
public ResponseEntity<Users> updateUsername(@PathVariable Long id, @RequestBody Users user) {
    // Solo accesible con token válido
}
```

---

### 2. `SessionTokenService` - Gestor de Tokens

Genera y valida tokens en memoria:

```java
@Service
public class SessionTokenService {
    private Map<Long, String> userTokens = new ConcurrentHashMap<>();

    public String generateTokenForUser(Long userId) {
        String token = java.util.UUID.randomUUID().toString();
        userTokens.put(userId, token);
        return token;
    }

    public boolean validateToken(Long userId, String token) {
        return token != null && token.equals(userTokens.get(userId));
    }
}
```

**Limitaciones:** Tokens en memoria (se pierden al reiniciar), sin expiración automática, una sesión por usuario.

---

### 3. `SecurityInterceptor` - Validador de Peticiones

### 3. `SecurityInterceptor` - Validador de Peticiones

Intercepta peticiones y valida tokens:

```java
@Component
public class SecurityInterceptor implements HandlerInterceptor {
    @Autowired
    private SessionTokenService sessionTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequiresAuth requiresAuth = handlerMethod.getMethodAnnotation(RequiresAuth.class);

            if (requiresAuth != null) {
                Long userId = extraerUserId(request);
                String token = request.getHeader("User-Token");

                if (!sessionTokenService.validateToken(userId, token)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return false; // Bloquea la petición
                }
            }
        }
        return true; // Permite continuar
    }

    private Long extraerUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("User-Id");
        if (userIdHeader != null) return Long.parseLong(userIdHeader);
        
        // Alternativa: extrae de URL /v1/api/users/{id}
        String path = request.getRequestURI();
        if (path.matches("/v1/api/users/\\d+.*")) {
            return Long.parseLong(path.split("/")[4]);
        }
        return null;
    }
}
```

**Flujo:** Captura peticiones → Verifica `@RequiresAuth` → Extrae credenciales → Valida → Permite/Bloquea

---

### 4. `WebMvcConfig` - Registro del Interceptor

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final SecurityInterceptor securityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/v1/api/**");
    }
}
```

---

### 5. Ejemplo de Controlador

```java
@RestController
@RequestMapping("/v1/api/users")
public class UserController {
    @Autowired private UserService userService;
    @Autowired private SessionTokenService sessionTokenService;

    //  PÚBLICO - Registro
    @PostMapping("")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid Users user) {
        Users createdUser = userService.create(user);
        String token = sessionTokenService.generateTokenForUser(createdUser.getId());
        return ResponseEntity.ok(new UserResponseDto(createdUser, token));
    }

    //  PROTEGIDO - Actualización
    @PutMapping("/{id}")
    @RequiresAuth
    public ResponseEntity<Users> updateUsername(@PathVariable Long id, @RequestBody Users user) {
        return ResponseEntity.ok(userService.updateUsername(id, user));
    }
}
```

---

## Componentes del Frontend

### 1. `AuthService` - Gestión de Sesión

Gestiona credenciales en `localStorage`:

```typescript
@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject = new BehaviorSubject<string | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private router: Router, private userService: UserService) {
    // Recupera sesión al iniciar
    const storedUser = localStorage.getItem('currentUser');
    const userId = localStorage.getItem('userId');
    const token = localStorage.getItem('userToken');
    
    if (storedUser && userId && token) {
      this.currentUserSubject.next(storedUser);
    } else {
      this.clearCurrentUser();
    }
  }

  setCurrentUser(username: string, userId?: number, token?: string): void {
    localStorage.setItem('currentUser', username);
    if (userId) localStorage.setItem('userId', userId.toString());
    if (token) localStorage.setItem('userToken', token);
    this.currentUserSubject.next(username);
  }

  getCurrentUserId(): number | null {
    const userId = localStorage.getItem('userId');
    return userId ? parseInt(userId) : null;
  }

  getUserToken(): string | null {
    return localStorage.getItem('userToken');
  }

  clearCurrentUser(): void {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('userId');
    localStorage.removeItem('userToken');
    this.currentUserSubject.next(null);
  }

  register(user: User): Observable<UserResponse> {
    return this.userService.create(user).pipe(
      tap(response => {
        this.setCurrentUser(response.user.username, response.user.id, response.token);
      })
    );
  }
}
```

---

### 2. `AuthInterceptor` - Inyección Automática de Headers

Añade automáticamente `User-Id` y `User-Token` a **todas** las peticiones HTTP:

```typescript
export const AuthInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const userId = authService.getCurrentUserId();
  const token = authService.getUserToken();

  if (userId && token) {
    request = request.clone({
      setHeaders: {
        'User-Id': userId.toString(),
        'User-Token': token
      }
    });
  }

  return next(request);
}
```

**Transformación:**
```typescript
// ANTES del interceptor:
GET /v1/api/rooms
Headers: { Content-Type: 'application/json' }

// DESPUÉS del interceptor:
GET /v1/api/rooms
Headers: {
  Content-Type: 'application/json',
  User-Id: '123',
  User-Token: '550e8400-e29b-41d4-a716-446655440000'
}
```

**Registro en `app.config.ts`:**
```typescript
export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptors([AuthInterceptor]))
  ]
};
```

---

## Flujo de Autenticación

###  Registro de Usuario

```
USUARIO                    FRONTEND                    BACKEND
   │                          │                           │
   │─1. Ingresa username──────>│                           │
   │                          │                           │
   │                          │─2. POST /v1/api/users────>│
   │                          │   { username: "Juan" }    │
   │                          │                           │
   │                          │                           │─3. Guarda en BD
   │                          │                           │   Genera token UUID
   │                          │                           │   Map.put(123, token)
   │                          │                           │
   │                          │<─4. { user, token }───────│
   │                          │                           │
   │                          │─5. Guarda en localStorage:│
   │                          │   - userId: 123           │
   │                          │   - userToken: "550e..."  │
   │                          │                           │
   │<─6. Sesión iniciada ✅───│                           │
```

---

### 🔒 Petición a Endpoint Protegido

```
USUARIO                    FRONTEND                    BACKEND
   │                          │                           │
   │─1. Actualiza username────>│                           │
   │                          │                           │
   │                          │─2. AuthInterceptor añade: │
   │                          │   User-Id: 123            │
   │                          │   User-Token: "550e..."   │
   │                          │                           │
   │                          │─3. PUT /v1/api/users/123─>│
   │                          │                           │
   │                          │                           │─4. SecurityInterceptor
   │                          │                           │   Detecta @RequiresAuth
   │                          │                           │   Valida token ✅
   │                          │                           │
   │                          │                           │─5. Controller ejecuta
   │                          │                           │   Actualiza BD
   │                          │                           │
   │                          │<─6. 200 OK────────────────│
   │<─7. Actualizado ✅───────│                           │
```

---

### ❌ Petición con Token Inválido

```
ATACANTE                   FRONTEND                    BACKEND
   │                          │                           │
   │─1. DELETE /v1/api/rooms/5────────────────────────────>│
   │   Headers:               │                           │
   │   User-Id: 999           │                           │
   │   User-Token: "FAKE"     │                           │
   │                          │                           │
   │                          │                           │─2. SecurityInterceptor
   │                          │                           │   Detecta @RequiresAuth
   │                          │                           │   validateToken() ❌
   │                          │                           │   return false
   │                          │                           │
   │<──────────3. 403 FORBIDDEN────────────────────────────│
```

