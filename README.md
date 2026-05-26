# Sistema de Consulta de Salas - OPEI

<p>
  <img src="docs/images/OPEI-logo.png" alt="Logo da OPEI" width="800">
</p>

Aplicação web mobile-first criada para permitir que participantes da OPEI consultem sua sala de prova por nome ou CPF.

## Fluxo principal

1. O aluno acessa o site pelo QR Code.
2. Digita parte do nome ou CPF.
3. O sistema exibe sugestões com nome e CPF mascarado.
4. O aluno seleciona seu nome.
5. O sistema exibe o card com sala, bloco, andar, modalidade e polo.

## Backend

O backend foi desenvolvido com Java e Spring Boot.

Principais responsabilidades:

- buscar alunos por nome ou CPF;
- retornar sugestões para autocomplete;
- mascarar CPF antes de enviar ao frontend;
- derivar bloco e andar a partir da sala;
- futuramente importar dados a partir de CSV.

## Frontend

O frontend do sistema foi desenvolvido com **React + Vite**, com foco em uma experiência **mobile-first**, considerando que a maioria dos participantes acessará o site pelo celular após escanear um QR Code nos locais de prova.

A interface foi pensada para ser simples, direta e alinhada à identidade visual da OPEI. O usuário informa seu nome ou CPF em um campo de busca, recebe sugestões em tempo real e, ao selecionar seu nome, visualiza um card com as principais informações da prova.

### Principais características

- Interface responsiva, priorizando o uso em smartphones.
- Campo de busca por nome ou CPF.
- Autocomplete com sugestões de alunos.
- Exibição de CPF mascarado para preservar a privacidade.
- Card de resultado com destaque visual para a sala.
- Exibição de bloco, andar, modalidade, polo e handle, quando disponível.
- Uso da identidade visual da OPEI, incluindo:
  - logo oficial;
  - tipografia Sora;
  - cores institucionais da marca;
  - detalhes visuais inspirados no material gráfico da edição.


### Tecnologias utilizadas no frontend

- React
- Vite
- JavaScript
- CSS
- React Icons

### Identidade visual

A interface utiliza elementos visuais inspirados no site e nos materiais oficiais da OPEI, como a paleta de cores da marca:

- Azul: `#15A6D5`
- Verde: `#42AF65`
- Amarelo: `#FFAD00`
- Vermelho: `#E6243D`
- Preto do rodapé: `#212121`

Além disso, o layout utiliza a fonte **Sora**, mantendo proximidade com a comunicação visual da olimpíada.

## Observação sobre dados sensíveis

O CPF completo não deve ser exposto no frontend.  
A _API_ retorna apenas CPF mascarado no formato:

```txt
123.***.***-00
```

## Status

Projeto ainda em desenvolvimento.