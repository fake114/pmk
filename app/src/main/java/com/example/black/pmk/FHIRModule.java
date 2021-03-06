package com.example.black.pmk;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.BundleTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.HTTPVerbEnum;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.client.IGenericClient;

public class FHIRModule {

    private final Iterable<Double> temperatures;
    Patient patient;


    public FHIRModule(Iterable<Double> temperatures, com.example.black.pmk.data.Patient rawPatient) {
        this.temperatures = temperatures;
        patient = createPatient(rawPatient);
    }

    public void doWork() {


        Bundle bundle = createBundle();
        connect();
        buildBundle(bundle, patient, createObservationsFromList(patient));
        log(bundle);
    }

    private void connect() {
        // We're connecting to a DSTU1 compliant server in this example
        FhirContext ctx = FhirContext.forDstu2();
        String serverBase = "http://fhirtest.uhn.ca/baseDstu2";
        ctx.getRestfulClientFactory().setSocketTimeout(1000000000);
        IGenericClient client = ctx.newRestfulGenericClient(serverBase);
    }

    private Patient createPatient(com.example.black.pmk.data.Patient rawPatient) {
        // Create a patient object
        Patient patient = new Patient();
        patient.addIdentifier()
                .setSystem("http://acme.org/mrns")
                .setValue("987654321");
        patient.addName()
                .addFamily(rawPatient.getName())
                .addGiven(rawPatient.getGivenName());
        patient.setGender(rawPatient.getGenderEnum());
        patient.setBirthDateWithDayPrecision(rawPatient.getBirthday());
        patient.setId(IdDt.newRandomUuid());
        return patient;
    }

    private Observation createObservation(Patient patient, double temperatureValue) {
        // Create an observation object
        Observation observation = new Observation();
        observation.setStatus(ObservationStatusEnum.FINAL);
        observation
                .getCode()
                .addCoding()
                .setSystem("http://loinc.org")
                .setCode("8310-5")
                .setCode("8328-7")
                .setDisplay("Axillary body temperature [Cel]");
        observation.setValue(
                new QuantityDt()
                        .setValue(temperatureValue)
                        .setUnit("degrees C")
                        .setSystem("http://unitsofmeasure.org")
                        .setCode("Cel"));
        observation.setSubject(new ResourceReferenceDt(patient.getId().getValue()));
        return observation;
    }


    private Bundle createBundle() {
        // Create a bundle that will be used as a transaction
        Bundle bundle = new Bundle();
        bundle.setType(BundleTypeEnum.TRANSACTION);
        return bundle;
    }

    private void buildBundle(Bundle bundle, Patient patient, List<Observation> observations) {
        // Add the patient as an entry. This entry is a POST with an
// If-None-Exist header (conditional create) meaning that it
// will only be created if there isn't already a Patient with
// the identifier 12345
        bundle.addEntry()
                .setFullUrl(patient.getId().getValue())
                .setResource(patient)
                .getRequest()
                .setUrl("Patient")
                .setIfNoneExist("Patient?identifier=http://acme.org/mrns|12345")
                .setMethod(HTTPVerbEnum.POST);
        // Add the observation. This entry is a POST with no header
// (normal create) meaning that it will be created even if
// a similar resource already exists.
        for (Observation observation : observations) {
            bundle.addEntry()
                    .setResource(observation)
                    .getRequest()
                    .setUrl("Observation")
                    .setMethod(HTTPVerbEnum.POST);
        }

    }

    private void log(Bundle bundle) {
        // Log the request
        FhirContext ctx = FhirContext.forDstu2();
        Log.i("TAG", ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle));
        //System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle));

        // Create a client and post the transaction to the server
        IGenericClient client = ctx.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu2");
        Bundle resp = client.transaction().withBundle(bundle).execute();

// Log the response

        Log.i("TAG", ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(resp));
        //System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(resp));
    }

    private List<Observation> createObservationsFromList(Patient patient) {
        List<Observation> observations = new ArrayList<>();
        for (Double d : temperatures) {
            observations.add(createObservation(patient, d));
        }
        return observations;
    }

}
