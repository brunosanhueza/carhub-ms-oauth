import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { UsuarioService } from '../../services/usuario-service';
import { Auth } from '../../services/auth';

const DOMINIOS_PERMITIDOS = ['gmail.com','hotmail.com','outlook.com'];

export function validadorDominioCorreo(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const emailUser = control.value;

    if (!emailUser) return null; //Si el espacio del campo de "correo electronico" esta vacio, el "validators.required" hara el trabajo de avisar que falta el correo.

    const arrobaIndex = emailUser.lastIndexOf('@');

    if (arrobaIndex === -1 || arrobaIndex === emailUser.length - 1) {
      return null;
    }

    const dominio = emailUser.substring(arrobaIndex + 1).trim().toLowerCase();

    const coincidenciaParcial = DOMINIOS_PERMITIDOS.some(d => d.startsWith(dominio));

    const esValido = DOMINIOS_PERMITIDOS.includes(dominio);

    if (esValido) {
      return null;
    }

    if (!coincidenciaParcial) {
      return { dominioInvalido: true };
    }

    return null;
  };
}

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  loginForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private usuarioService: UsuarioService,
    private authService: Auth
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      emailUser: ['', [Validators.required, Validators.email, validadorDominioCorreo()]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      const email = this.loginForm.value.emailUser;
      const password = this.loginForm.value.password;
      console.log(this.loginForm.value);

      this.usuarioService.loginUsuario(email, password).subscribe({
        next: (usuario) => {
          localStorage.setItem('userLogged', JSON.stringify(usuario));
          this.router.navigate(['/dashboard']);

        },
        error: (err) => {
          console.error('credenciales incorrectas...: ', err);
          alert('el correo o la contraseña o ambos es incorrecto');
        }
      })
    }
  }

  async loginWithMicrosoft(): Promise<void> {
    try {
      const usuario = await this.authService.loginMicrosoft();
      console.log('Sesión iniciada con Microsoft Entra ID:', usuario);
      this.router.navigate(['/dashboard']);
    } catch (error) {
      console.error('Error al iniciar sesión con Microsoft:', error);
      alert('Error o inicio de sesión cancelado con Microsoft Entra ID.');
    }
  }

  ingresoInvitado(): void {
    console.log('Ingresando como invitado');
    this.router.navigate(['/dashboard']);
  }
}
