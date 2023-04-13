package me.xezard.devmc.drazex.discord.domain.model.web.requests

class PastePost (
    var files: List<File>
)
class File(
    var content: FileContent
)
class FileContent(
    var format: String,
    var value: String
)