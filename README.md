# Numberle
> [!IMPORTANT]
> `Numberle` is a mathematical equation guessing game where players must accurately guess a randomly generated equation within `six attempts`. Players input their `equation`, and the goal is to match the `target equation`. In the calculation, players can use `numbers (0-9)` and `arithmetic symbols (+ - * / =)`.

<div align="left">
  <img src="https://img.shields.io/badge/-Java-blue.svg">
  <img src="https://img.shields.io/badge/-Junit-green.svg">
  <img src="https://img.shields.io/badge/-Swing-orange.svg">
</div>

## Overview of Features

| Feature       | Description                                                      |
|---------------|------------------------------------------------------------------|
| Basic Logic   | Implement core functions like equation validation, user input handling, and color feedback. |
| Multi-Version Interface | Offers graphical user interface (GUI) and command line interface (CLI). |
| Architecture Design | GUI version follows the Model-View-Controller (MVC) architecture, and the CLI version reuses the model from the GUI version. |
| Testing and Documentation | Complete unit tests, documentation, and provide class diagrams with detailed comments. |

## Personal Contributions

> [!IMPORTANT]
> 1. **Requirements Analysis**: Understand the functional and non-functional requirements of the documentation.
> 2. **Design Pattern**: Use the Model-View-Controller (MVC) design pattern to complete the GUI and CLI versions.
> 3. **Class Diagram Design**: Draw class diagrams, including fields, functions, and relationships between classes, ensuring clear short-term and long-term planning.
> 4. **GUI Architecture Implementation**:
>    - Use the `Observable` class’s `SetChanged()` and `notifyObservers()` methods to notify `NumberleView` for view updates.
>    - Use `NumberleController` to manage the interaction logic in the GUI.
> 5. **Pre- and Post-Condition Declarations**: Add preconditions and postconditions for all public methods in the `NumberleModel` class, using `assert` for condition checks.
> 6. **Equation Legality Verification**: Use regular expressions to verify the legality of equations.
> 7. **UI Construction**: Build the GUI using the Swing framework, with layout arranged using the Box layout.
> 8. **Multiple Entry Points**: Provide different entry points for running the GUI and CLI versions.
> 9. **Flag Settings**:
>    - Error Information Display: If set, it shows error messages when the equation is invalid.
>    - Display for Testing Purposes: If set, displays the target equation.
>    - Equation Selection Mode: If set, the equation is randomly selected; otherwise, a fixed equation is used.
> 10. **Unit Testing**: Use the JUnit testing tool to conduct unit tests on `INumberleModel` to verify expected outputs for various inputs.

## Demo
> [!NOTE]
> The first two images are from the CLI version, while the last four are illustrations of the GUI version in operation.
<div align="left">
  <img src="https://github.com/user-attachments/assets/997e715d-0ed9-44e6-bc8e-1edc3d0832db" width="400">
  <img src="https://github.com/user-attachments/assets/861d63ff-2871-4a2c-bc29-dd284a5c4cc1" width="400">
  <img src="https://github.com/user-attachments/assets/06ed9631-aa65-450e-b5b3-00eeedcbcb18" width="400">
  <img src="https://github.com/user-attachments/assets/c733cc85-59cf-4274-b8dd-c9d68b11cc05" width="400">
  <img src="https://github.com/user-attachments/assets/03531c21-3763-4aed-8e49-65b8af6d386d" width="400">
  <img src="https://github.com/user-attachments/assets/bffa8fdd-980a-4113-b3a6-72b583804b59" width="405">
</div>

## Programming Technologies

| Technology    | Description                       |
|---------------|-----------------------------------|
| Programming Language | Java                         |
| Frameworks/Libraries | Swing, JUnit                  |
| Design Pattern | Model-View-Controller (MVC)      |

## Implementation Details

| Aspect        | Description                                                    |
|---------------|---------------------------------------------------------------|
| Basic Game Logic | Implement core functions like equation validation and user interaction logic. |
| Interface     | Implement both graphical (GUI) and command line (CLI) interfaces. |
| Architecture  | Comply with MVC standards, ensuring clear and maintainable code structure. |
| Testing and Documentation | Complete unit tests with detailed documentation, including class diagrams and code comments. |

## How to Use
> [!NOTE]
> 1. Clone this repository:
>   ```sh
>   git clone https://github.com/DarrylXzq/Numberle.git
>   ```
> 2. Import the project into your Integrated Development Environment (IDE).
> 3. Compile and run:
>   - **GUI Version**: Run `GUIAPP.java`
>   - **CLI Version**: Run `CLIAPP.java`
> 4. Use flags to experience a variety of gameplay.

## Usage Restrictions
> [!WARNING]
> 1. This project and its code may `not` be used for any form of `commercial sales or services`.
> 2. The project must `not` be used as or embedded in any `commercial product`.

## 😄 Acknowledgements

 - Thanks to the family, supervisors, and friends for their help.👋👋👋
 - [github-readme-stats](https://github.com/anuraghazra/github-readme-stats/blob/master/readme.md)
 - [Awesome Readme Templates](https://awesomeopensource.com/project/elangosundar/awesome-README-templates)
 - [Awesome README](https://github.com/matiassingers/awesome-readme)
 - [How to write a Good readme](https://bulldogjob.com/news/449-how-to-write-a-good-readme-for-your-github-project)

## 👋 Feedback

If you have any feedback, please reach out to us at `xiangzq.darryl@gmail.com`
