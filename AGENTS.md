# Markdown Authoring & Linting Standards

Whenever writing, editing, or generating Markdown files (`.md`):

1. **Always Specify Language in Fenced Code Blocks (MD040)**:
   - Every fenced code block (\`\`\`) MUST specify an exact language tag (e.g. \`text\`, \`powershell\`, \`bash\`, \`json\`, \`kotlin\`, \`typescript\`, \`sql\`, \`yaml\`, \`xml\`).
   - Never use bare triple backticks without a language tag.

2. **Surround Headings with Blank Lines (MD022)**:
   - Every heading (\`#\`, \`##\`, \`###\`, \`####\`) MUST have at least one blank line above it and at least one blank line below it.

3. **Surround Code Blocks and Lists with Blank Lines (MD031, MD032)**:
   - Every fenced code block and list (bullet or numbered) MUST be preceded and followed by a blank line.

4. **No Trailing Colons on Headings (MD026)**:
   - Do not end headings with punctuation such as colons (\`:\`). Use \`#### Step 1: Description\` or \`#### Description\`, not \`#### Description:\`.

5. **No Trailing Whitespace (MD009)**:
   - Do not leave trailing spaces at the end of lines. For line breaks in Markdown, use semantic paragraph breaks.
