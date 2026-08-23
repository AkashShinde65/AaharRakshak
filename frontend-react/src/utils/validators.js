export function isEmail(value) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
}

export function validateLogin(values) {
  const errors = {};
  if (!values.identifier?.trim()) errors.identifier = 'Email or mobile number is required.';
  if (!values.password) errors.password = 'Password is required.';
  return errors;
}

export function validateRegister(values) {
  const errors = {};
  if (!values.fullName?.trim()) errors.fullName = 'Full name is required.';
  if (!isEmail(values.email)) errors.email = 'Enter a valid email address.';
  if (!/^[6-9]\d{9}$/.test(values.mobileNumber || '')) {
    errors.mobileNumber = 'Enter a valid 10-digit Indian mobile number.';
  }
  if ((values.password || '').length < 8) errors.password = 'Password must be at least 8 characters.';
  if (values.password !== values.confirmPassword) errors.confirmPassword = 'Passwords do not match.';
  if (!values.acceptPrivacy) errors.acceptPrivacy = 'Privacy consent is required.';
  return errors;
}

export function validateComplaint(values) {
  const errors = {};
  if (!values.complaintType) errors.complaintType = 'Select a complaint type.';
  if (!values.category) errors.category = 'Select a complaint category.';
  if (values.complaintType === 'PACKAGED_FOOD' && !values.productName?.trim()) {
    errors.productName = 'Product name is required for packaged food.';
  }
  if (values.complaintType === 'PREPARED_DISH' && !values.vendorName?.trim()) {
    errors.vendorName = 'Vendor or location name is required for dish complaints.';
  }
  if (!values.description || values.description.trim().length < 20) {
    errors.description = 'Add at least 20 characters describing the issue.';
  }
  if (!values.address?.trim()) errors.address = 'Address or landmark is required.';
  if (!values.gpsConsent) errors.gpsConsent = 'GPS/location consent is required before submission.';
  return errors;
}

export function validateTracking(values) {
  const errors = {};
  if (!/^ARK-[A-Z0-9-]{6,}$/i.test(values.ticketNumber || '')) {
    errors.ticketNumber = 'Enter a valid complaint tracking number, for example ARK-SEED-0006.';
  }
  return errors;
}
