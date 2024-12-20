import org.clechasseur.adventofcode2020.Day4Data

object Day4 {
    private val input = Day4Data.input.toPassports()

    fun part1(): Int = input.count { it.valid }

    fun part2(): Int = input.count { it.valuesValid }

    private class Passport(val fields: Map<String, String>) {
        companion object {
            val requiredFields = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
            val optionalFields = listOf("cid")

            val hgtRegex = """^(\d+)(cm|in)$""".toRegex()
            val hclRegex = """^#[0-9a-fA-F]{6}$""".toRegex()
            val eclRegex = """^(amb|blu|brn|gry|grn|hzl|oth)$""".toRegex()
            val pidRegex = """^\d{9}$""".toRegex()

            val validations = mapOf<String, (String) -> Boolean>(
                "byr" to { value -> (value.toIntOrNull() ?: 0) in 1920..2002 },
                "iyr" to { value -> (value.toIntOrNull() ?: 0) in 2010..2020 },
                "eyr" to { value -> (value.toIntOrNull() ?: 0) in 2020..2030 },
                "hgt" to { value ->
                    when (val match = hgtRegex.matchEntire(value)) {
                        null -> false
                        else -> {
                            val (height, unit) = match.destructured
                            when (unit) {
                                "cm" -> (height.toIntOrNull() ?: 0) in 150..193
                                else -> (height.toIntOrNull() ?: 0) in 59..76
                            }
                        }
                    }
                },
                "hcl" to { value -> hclRegex.matches(value) },
                "ecl" to { value -> eclRegex.matches(value) },
                "pid" to { value -> pidRegex.matches(value) }
            )
        }

        val valid: Boolean get() = requiredFields.all { fields.containsKey(it) }
        val valuesValid: Boolean = valid && requiredFields.all { validations[it]!!(fields[it]!!) }
    }

    private fun String.toPassports(): List<Passport> {
        val passports = mutableListOf<Passport>()
        val fields = mutableMapOf<String, String>()
        lineSequence().forEach { line ->
            if (line.isEmpty()) {
                passports.add(Passport(fields.toMap()))
                fields.clear()
            } else {
                line.split(' ').forEach { fieldAndValue ->
                    val (field, value) = fieldAndValue.split(':')
                    fields[field] = value
                }
            }
        }
        if (fields.isNotEmpty()) {
            passports.add(Passport(fields.toMap()))
        }
        return passports
    }
}