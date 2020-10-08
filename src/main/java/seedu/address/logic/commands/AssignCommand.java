package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_APPOINTMENTS;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.Date;
import seedu.address.model.appointment.Time;
import seedu.address.model.person.Nric;
import seedu.address.model.person.Person;

/**
 * Assigns an appointment to Nuudle.
 */
public class AssignCommand extends Command {

    public static final String COMMAND_WORD = "assign";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Assigns an appointment to Nuudle. "
            + "Parameters: "
            + "PATIENT INDEX (must be a positive integer) "
            + PREFIX_DATE + "DATE "
            + PREFIX_TIME + "TIME \n"
            + "Example: " + COMMAND_WORD + " "
            + "1 "
            + PREFIX_DATE + "2018-12-12 "
            + PREFIX_TIME + "16:00:00";

    public static final String MESSAGE_SUCCESS = "New Appointment added: %1$s";
    public static final String ASSIGNMENT_OVERLAP = "This time slot is occupied";
    public static final String DATE_MISSING = "The date of appointment is missing";
    public static final String TIME_MISSING = "The time of appointment is missing";

    private final Index targetIndex;
    private final AssignLoader assignLoader;
    /**
     * Creates an AssignCommand to add a new {@code Appointment}
     * @param targetIndex index of the patient in the list.
     * @param assignLoader details of an appointment.
     */
    public AssignCommand(Index targetIndex, AssignLoader assignLoader) {
        requireAllNonNull(targetIndex, assignLoader);

        this.targetIndex = targetIndex;
        this.assignLoader = new AssignLoader(assignLoader);
    }

    // todo
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownPatientList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownPatientList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person patient = lastShownPatientList.get(targetIndex.getZeroBased());
        Appointment appointment = createAppointment(patient, assignLoader);

        if (model.hasAppointment(appointment)) {
            throw new CommandException(ASSIGNMENT_OVERLAP);
        }

        model.addAppointment(appointment);
        model.updateFilteredAppointmentList(PREDICATE_SHOW_ALL_APPOINTMENTS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, appointment));
    }

    /**
     * Creates and returns an {@code Appointment} with merged details of {@code patient}
     * and {@code assignAppointmentBuilder}
     */
    private static Appointment createAppointment(Person patient,
            AssignLoader assignLoader) {
        assert patient != null;
        assert assignLoader.getDate().isPresent();
        assert assignLoader.getTime().isPresent();

        Date assignedDate = assignLoader.getDate().get();
        Time assignedTime = assignLoader.getTime().get();

        // Id is currently the name of patient.
        Nric assignedPatientId = patient.getNric();

        return new Appointment(assignedDate, assignedTime, assignedPatientId);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AssignCommand // instanceof handles nulls
                && targetIndex.equals(((AssignCommand) other).targetIndex)); // state check
    }

    /**
     * Stores the details to the appointment to be assigned.
     * Each non=empty field value will replace the corresponding field value of the Appointment.
     */
    public static class AssignLoader {
        private Date date;
        private Time time;
        private Nric patientId; //todo: modify when Nric class is out

        public AssignLoader() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code patientId} is used internally.
         */
        public AssignLoader(AssignLoader toCopy) {
            setAppointmentDate(toCopy.date);
            setAppointmentTime(toCopy.time);
            setPatientId(toCopy.patientId);
        }

        /**
         * Returns true if at least one field has been set.
         */
        public boolean isAnyFieldSet() {
            return CollectionUtil.isAnyNonNull(date, time, patientId);
        }

        /**
         * Sets {@code date} of this AssignAppointmentBuilder object.
         */
        public void setAppointmentDate(Date date) {
            this.date = date;
        }

        /**
         * Gets {@code date} of this AssignAppointmentBuilder object.
         */
        public Optional<Date> getDate() {
            return Optional.ofNullable(date);
        }

        /**
         * Sets {@code time} of this AssignAppointmentBuilder object.
         */
        public void setAppointmentTime(Time time) {
            this.time = time;
        }

        /**
         * Gets {@code time} of this AssignAppointmentBuilder object.
         */
        public Optional<Time> getTime() {
            return Optional.ofNullable(time);
        }

        /**
         * Sets {@code patientId} of this AssignAppointmentBuilder object.
         */
        public void setPatientId(Nric patientId) {
            this.patientId = patientId;
        }

        /**
         * Gets the {@code patientId} of this AssignAppointmentBuilder object.
         */
        public Optional<Nric> getPatientId() {
            return Optional.ofNullable(patientId);
        }
    }
}
