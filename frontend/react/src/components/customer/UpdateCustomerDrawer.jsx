import { Button, Drawer, DrawerBody, DrawerCloseButton, DrawerContent, DrawerFooter, DrawerHeader, DrawerOverlay, useDisclosure } from "@chakra-ui/react"
import UpdateCustomerForm from "./UpdateCustomerForm";

const AddIcon = () => "+";
const CloseIcon = () => "x";

const UpdaterCustomerDrawer = ({ fetchCustomers, initialValues, customerId }) => {
    const { isOpen, onOpen, onClose } = useDisclosure();

    return <>
        <Button
          bg={'gray.200'}
          color='black'
          rounded={'full'}
          _hover={{
            transform: 'translateY(-2px)',
            boxShadow: 'lg'
          }}
          _focus={{
            bg: 'grey.500'
          }}
          onClick={onOpen}>
          Update
        </Button>

        <Drawer isOpen={isOpen} onClose={onClose} size={"xl"}>
          <DrawerOverlay />
          <DrawerContent>
            <DrawerCloseButton />
            <DrawerHeader>Update customer</DrawerHeader>
  
            <DrawerBody>
              <UpdateCustomerForm
                fetchCustomers={fetchCustomers}
                initialValues={initialValues}
                customerId={customerId}
              />
            </DrawerBody>
  
            <DrawerFooter>
            <Button
            leftIcon={<CloseIcon />}
            colorScheme={"teal"}
            onClick={onClose}
        >
            Close
        </Button>
            </DrawerFooter>
          </DrawerContent>
        </Drawer>
    </>
    
}

export default UpdaterCustomerDrawer;