## 2024-05-14 - Compose Material Bottom Sheet Keyboard Usability
**Learning:** By default, standard Compose text inputs in a bottom sheet (`ModalBottomSheet`) require an explicit tap to focus. Furthermore, pressing "Enter" on the software keyboard does nothing out-of-the-box.
**Action:** When implementing modal forms with text inputs, always remember to add `LaunchedEffect` with `FocusRequester` to auto-focus the first field, and bind `KeyboardOptions(imeAction = ImeAction.Done)` to a `KeyboardActions(onDone = { ... })` block to streamline submission!
