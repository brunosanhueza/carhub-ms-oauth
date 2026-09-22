import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UsuarioService } from '../../services/usuario-service';
import { Usuario } from '../../models/usuario/usuario';
import { validadorDominioCorreo } from '../../utils/email-util/email-util';





@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.component.css',
})

export class Register implements OnInit {
  registerForm!: FormGroup;

  constructor(
    private fb: FormBuilder, 
    private router: Router,
    private usuarioService: UsuarioService
  ) {}



  ngOnInit(): void {
    this.registerForm = this.fb.group({
      nameUser: ['', [Validators.required, Validators.minLength(3)]], //fsajfakja
      emailUser: ['', [Validators.required, Validators.email, validadorDominioCorreo()]], 
      passwordUser: ['', [Validators.required, Validators.minLength(6)]],
      phoneUser: ['', [Validators.required, Validators.maxLength(9),Validators.minLength(9)]]
    });
  }

  onSubmit(): void{
    // confirmaciones en consola, no son necesarios en realidad xd
    // console.log('formulario valido?',this.registerForm.valid);
    // console.log('valores: ', this.registerForm.value);
    if (this.registerForm.valid){
      const nuevoUsuario: Usuario = this.registerForm.value;

      this.usuarioService.crearUsuario(nuevoUsuario).subscribe({
        next: (res) => {
          console.log('Usuario registrado: ', res);
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          console.error('Error al crear usuario: ', err);
        }
      })
    }
  }
  }
